package com.narara.superboard.common.application.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.narara.superboard.common.enums.MessageOrigin;
import com.narara.superboard.common.infrastructure.redis.RedisService;
import com.narara.superboard.common.interfaces.dto.MessageRecord;
import com.narara.superboard.common.interfaces.dto.OffsetKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.event.ListenerContainerIdleEvent;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventListenerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisService redisService;
    private final ConsumerFactory<String, String> consumerFactory;
    // 컨슈머 리스너 중복 관리를 위한 맵
    private final Map<String, ConcurrentMessageListenerContainer<String, String>> activeListeners;
    private final Map<OffsetKey, Acknowledgment> pendingAcks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long WAIT_PERIOD = 3000;

    public void processSubscriptionEvent(SessionSubscribeEvent event) {
        String destination = (String) event.getMessage().getHeaders().get("simpDestination");

        String[] destinationParts = destination.split("/");
        if (destinationParts.length < 6) return;

        String entityType = destinationParts[2];
        Long primaryId = Long.parseLong(destinationParts[3]);
        Long memberId = Long.parseLong(destinationParts[5]);

        String topic = entityType + "-" + primaryId;
        String groupId = "member-" + memberId;
        String listenerKey = topic + "-" + groupId;

        ConcurrentMessageListenerContainer<String, String> container;

        // 중복 리스너 확인
        if (activeListeners.containsKey(listenerKey)) {
            container = activeListeners.get(listenerKey);

        } else {
            // Kafka Listener 컨테이너 팩토리 설정
            ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(createConsumerFactory(groupId));
            factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

            container = factory.createContainer(topic);

            // 컨슈머 리스너 등록
            container.setupMessageListener((AcknowledgingMessageListener<String, String>) (record, acknowledgment) -> {
                processRecord(record, acknowledgment, entityType, primaryId, memberId);
            });

            container.start();

            activeListeners.put(listenerKey, container);
        }

        // 구독 시점에 필요한 Offset부터 밀린 메시지 가져오기
        fetchMissedMessages(container, topic, memberId);
    }

    /**
     * 구독 시점에 필요한 Offset부터 밀린 메시지를 가져와 STOMP로 전송
     * - 지정된 container를 멈추고 리스너를 설정하여 누락된 메시지를 다시 가져옴
     */
    private void fetchMissedMessages(ConcurrentMessageListenerContainer<String, String> container, String topic, Long memberId) {
        container.stop();
        container.getContainerProperties().setMessageListener((AcknowledgingMessageListener<String, String>) (record, acknowledgment) -> {
            processRecord(record, acknowledgment, topic.split("-")[0], Long.parseLong(topic.split("-")[1]), memberId);
        });
        container.start();
    }

    /**
     * 메시지를 STOMP로 전송하고, acknowledgment를 대기 목록에 추가
     */
    private void processRecord(ConsumerRecord<String, String> record, Acknowledgment acknowledgment, String entityType, Long primaryId, Long memberId){
        long offset = record.offset();
        String destination = "/topic/" + entityType + "/" + primaryId + "/member/" + memberId;

        // header에 offset을 포함해 STOMP로 메시지 전송
        Map<String, Object> headers = new HashMap<>();
        headers.put("offset", offset);
        headers.put("type", MessageOrigin.RECEIVED.toString());
        messagingTemplate.convertAndSend(destination, record.value(), headers);

        // 메모리 맵과 Redis에 ACK 추가
        OffsetKey offsetKey = new OffsetKey(record.topic(), record.partition(), offset, "member-" + memberId);

        //pendingAcks는 acknowledgment를 임시로 저장하고 있는것
        pendingAcks.put(offsetKey, acknowledgment);
        // ACK 대기 큐에 추가
        redisService.addAckToQueue(entityType+"-"+primaryId, "member-"+memberId, offset, record.value());
    }

    /**
     * offset 지정한 전체 데이터 받을 시에, 마지막 offset으로 ACK 받기
     */
    public void processAcknowledgment(OffsetKey offsetKey) {
        String topic = offsetKey.topic();
        String groupId = offsetKey.groupId();
        long offset = offsetKey.offset();

        // Redis에서 마지막으로 ACK된 오프셋 조회, null이면 -1로 저장
        Long lastOffset = redisService.getLastAcknowledgedOffset(topic, groupId);

        Acknowledgment acknowledgment = pendingAcks.get(offsetKey);

        if (offset == lastOffset + 1) {
            if (acknowledgment != null) {
                // Kafka에 오프셋 커밋
                acknowledgment.acknowledge();
                pendingAcks.remove(offsetKey);

                // acknowledgment가 null이 아닐 때에만 Redis에 최신 오프셋 갱신
                redisService.setLastAcknowledgedOffset(topic, groupId, offset);

                // 다음 오프셋을 대기 큐에서 처리
                processPendingAcks(topic, groupId, offset + 1);
            } else {
                log.warn("acknowledgment가 null이므로 오프셋 갱신을 건너뜁니다 - 오프셋: {}", offset);
            }
        } else {
            redisService.addAckToQueue(topic, groupId, offset, "ack_pending");
            log.warn("ACK 순서가 맞지 않습니다. 대기 큐에 저장 - 예상 오프셋: {}, 실제 오프셋: {}", lastOffset + 1, offset);
            // 3초동안 기다림
            startSingleWaitTimer(offsetKey);
        }
    }

    /**
     * 대기 큐에서 순차적으로 ACK를 처리
     */
    private void processPendingAcks(String topic, String groupId, long startOffset) {
        Long nextOffset = startOffset;

        while (true) {
            // 다음 ack가 왔는지 확인
            String pendingAck = (String) redisService.getAckFromQueue(topic, groupId, nextOffset);

            if (pendingAck != null) {

                Acknowledgment acknowledgment = pendingAcks.remove(new OffsetKey(topic, 0, nextOffset, groupId));
                if (acknowledgment != null) {
                    acknowledgment.acknowledge(); // ACK 처리

                    // 최신 오프셋 갱신
                    redisService.setLastAcknowledgedOffset(topic, groupId, nextOffset); // 최신 오프셋 갱신

                    // 대기 큐에서 ACK 제거
                    redisService.removeAckFromQueue(topic, groupId, nextOffset);
                    nextOffset++;
                } else {
                    log.warn("pendingAcks에 해당 오프셋에 대한 acknowledgment가 없습니다 - 토픽: {}, 그룹 ID: {}, 오프셋: {}", topic, groupId, nextOffset);
                }
            } else {
                log.info("더 이상 대기 중인 ACK가 없습니다 - 토픽: {}, 그룹 ID: {}, 앞으로 처리 할 오프셋: {}", topic, groupId, nextOffset);
                break;
            }
        }
    }

    /**
     * ACK가 순서대로 오지 않는 경우에 호출
     * - WAIT_PERIOD 동안 ACK가 도착하지 않으면 대기 시간 초과로 ACK가 필요 없다고 판단하고 제거
     */
    private void startSingleWaitTimer(OffsetKey offsetKey) {
        scheduler.schedule(() -> {
            String pendingAck = (String) redisService.getAckFromQueue(offsetKey.topic(), offsetKey.groupId(), offsetKey.offset());
            if (pendingAck != null) {
                pendingAcks.remove(offsetKey);
                redisService.removeAckFromQueue(offsetKey.topic(), offsetKey.groupId(), offsetKey.offset());
            }
        }, WAIT_PERIOD, TimeUnit.MILLISECONDS);
    }

    /**
     * 엔터티별 Consumer Group을 위한 Kafka ConsumerFactory 생성
     */
    private ConsumerFactory<String, String> createConsumerFactory(String groupId) {
        Map<String, Object> props = new HashMap<>(consumerFactory.getConfigurationProperties());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return new DefaultKafkaConsumerFactory<>(props);
    }


}

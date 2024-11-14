package com.narara.superboard.common.application.kafka;

import com.narara.superboard.common.infrastructure.redis.RedisService;
import com.narara.superboard.common.interfaces.dto.MessageRecord;
import com.narara.superboard.common.interfaces.dto.OffsetKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
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
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventListenerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisService redisService;
    private final ConsumerFactory<String, String> consumerFactory;
    // 컨슈머 리스너 중복 관리를 위한 맵
    private final Map<String, ConcurrentMessageListenerContainer<String, String>> activeListeners = new ConcurrentHashMap<>();
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
     * 이미 존재하는 리스너를 사용하여 특정 오프셋에서부터 데이터를 다시 가져옴
     *
     * @param partition   파티션 번호
     * @param offset      시작할 오프셋
     * @param entityType  엔티티 타입
     * @param primaryId   기본 ID (엔티티의 ID)
     * @param memberId    회원 ID
     */
    public void seekToOffsetAndFetch(int partition, long offset, String entityType, Long primaryId, Long memberId) {

        // 해당 토픽에 대한 기존 리스너 확인
        String topic = entityType + "-" + primaryId;
        String groupId = "member-" + memberId;
        String listenerKey = topic + "-" + groupId;
        ConcurrentMessageListenerContainer<String, String> container = activeListeners.get(listenerKey);

        if (container == null) {
            log.error("지정된 토픽에 대한 리스너를 찾을 수 없습니다 - 토픽: {}", topic);
            return;
        }

        container.stop();
        // 리스트에 offset 넣기
        List<MessageRecord> messages = new ArrayList<>();
        AtomicReference<Acknowledgment> lastAcknowledgment = new AtomicReference<>(); // 마지막 ack 저장용

        // 리스너 설정: 오프셋 설정 후 다시 데이터 가져오기
        container.getContainerProperties().setMessageListener(
                (AcknowledgingConsumerAwareMessageListener<String, String>) (record, acknowledgment, consumer) -> {
                    seekToOffset((Consumer<String,String>) consumer, topic, partition, offset);
                    // 오프셋 이동 후 데이터 가져오기
                    consumer.resume(Collections.singleton(new TopicPartition(topic, partition))); // 특정 파티션에서 재개
                    // 메시지를 리스트에 추가
                    messages.add(new MessageRecord(record.offset(),record.value()));
                    // 마지막 acknowledgment 갱신
                    lastAcknowledgment.set(acknowledgment);
                }
        );

        // 메시지 리스트를 소켓 전송
        // header에 offset을 포함해 STOMP로 메시지 전송
        String destination = "/topic/" + entityType + "/" + primaryId + "/member/" + memberId;
        Map<String, Object> headers = new HashMap<>();
        // 마지막 오프셋 넣기
        headers.put("offset", messages.getLast().offset());
        messagingTemplate.convertAndSend(destination, messages, headers);

        // 마지막 acknowledgment로 ACK 커밋을 위한 처리
        Acknowledgment finalAck = lastAcknowledgment.get();
        if (finalAck != null) {
            OffsetKey offsetKey = new OffsetKey(topic, partition, messages.getLast().offset(), groupId);
            // TODO : ack를 받아서 pendingAcks를 체크할때, 똑같은 ack를 받으면 오또캐?
            // 다른 map을 해야하려나
            pendingAcks.put(offsetKey, finalAck); // 마지막 ACK만 저장
        }

        container.start(); // 컨슈머 재시작
        log.info("특정 오프셋으로 이동 후 메시지 가져오는 중 - 토픽: {}, 파티션: {}, 오프셋: {}", topic, partition, offset);
    }

    /**
     * 컨슈머를 지정한 오프셋으로 이동 및 재개
     */
    private void seekToOffset(Consumer<String, String> consumer, String topic, int partition, long offset) {
        TopicPartition topicPartition = new TopicPartition(topic, partition);
        consumer.pause(Collections.singleton(topicPartition)); // 특정 파티션 일시 중지
        consumer.seek(topicPartition, offset); // 오프셋 이동
        log.info("컨슈머 오프셋 이동 - 토픽: {}, 파티션: {}, 오프셋: {}", topic, partition, offset);
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
        messagingTemplate.convertAndSend(destination, record.value(), headers);

        // 메모리 맵과 Redis에 ACK 추가
        OffsetKey offsetKey = new OffsetKey(record.topic(), record.partition(), offset, "member-" + memberId);

        //pendingAcks는 acknowledgment를 임시로 저장하고 있는것
        pendingAcks.put(offsetKey, acknowledgment);
        // ACK 대기 큐에 추가
        redisService.addAckToQueue(entityType+"-"+primaryId, "member-"+memberId, offset, record.value());
    }

    /**
     * 순서에 맞는 ACK가 처리되었는지 확인하고, 순서가 맞지 않으면 Redis 대기 큐에 저장하여 순서를 맞춥니다.
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

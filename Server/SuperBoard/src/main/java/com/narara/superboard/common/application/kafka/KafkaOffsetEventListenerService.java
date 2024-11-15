package com.narara.superboard.common.application.kafka;

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
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaOffsetEventListenerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisService redisService;
    // 컨슈머 리스너 중복 관리를 위한 맵
    private final Map<String, ConcurrentMessageListenerContainer<String, String>> activeListeners;
    private final Map<OffsetKey, Acknowledgment> pendingLastAcks = new ConcurrentHashMap<>();

    /**
     * 특정 오프셋부터 데이터를 가져오는 메서드
     * @param partition   파티션 번호
     * @param startOffset      시작할 오프셋
     * @param entityType  엔티티 타입
     * @param primaryId   기본 ID (엔티티의 ID)
     * @param memberId    회원 ID
     */
    public void seekToEndAndFetch(int partition, long startOffset, String entityType, Long primaryId, Long memberId) {
        String topic = entityType + "-" + primaryId;
        String groupId = "member-" + memberId;
        String listenerKey = topic + "-" + groupId;

        ConcurrentMessageListenerContainer<String, String> container = activeListeners.get(listenerKey);
        if (container == null) {
            log.error("지정된 토픽에 대한 리스너를 찾을 수 없습니다 - listenerKey: {}", listenerKey);
        }

        List<MessageRecord> messages = new ArrayList<>();

        container.stop();

        // 컨슈머 오프셋 설정
        container.getContainerProperties().setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {
            @Override
            public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
                TopicPartition topicPartition = new TopicPartition(topic, partition);
                if (partitions.contains(topicPartition)) {
                    consumer.seek(topicPartition, startOffset);
                }
            }
        });

        // 메시지 수집 리스너 설정
        container.getContainerProperties().setMessageListener(
                (AcknowledgingConsumerAwareMessageListener<String, String>) (record, acknowledgment, consumer) -> {
                    try {
                        MessageRecord messageRecord = MessageRecord.of(record.offset(), record.value());
                        if (messageRecord != null) {
                            messages.add(messageRecord);
                            // lastAcknowledgment.set(acknowledgment);

                            // 마지막 오프셋인지 확인
                            long endOffset = getEndOffset(consumer, topic, partition);
                            if (record.offset() == endOffset - 1) {
                                // 마지막 오프셋 도달 시 메시지 전송 및 ACK 처리
                                sendMessagesToStomp(entityType,primaryId,memberId,messages);

                                // 메모리 맵과 Redis에 ACK 추가
                                OffsetKey offsetKey = new OffsetKey(record.topic(), record.partition(), record.offset(), "member-" + memberId);

                                //pendingAcks는 acknowledgment를 임시로 저장하고 있는것
                                pendingLastAcks.put(offsetKey, acknowledgment);
                            }
                        }
                    } catch (Exception e) {
                        log.error("메시지 변환 오류 - 오프셋: {}, 메시지: {}", record.offset(), record.value(), e);
                    }
                }
        );

        container.start();
    }

    /**
     * Kafka 컨슈머로부터 지정된 파티션의 마지막 오프셋을 가져오는 메서드
     */
    private long getEndOffset(Consumer<?, ?> consumer, String topic, int partition) {
        TopicPartition topicPartition = new TopicPartition(topic, partition);
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(Collections.singleton(topicPartition));
        return endOffsets.get(topicPartition);
    }

    /**
     * 메시지를 STOMP로 전송
     */
    private void sendMessagesToStomp(String entityType, Long primaryId, Long memberId, List<MessageRecord> messages) {
        if (messages.isEmpty()) {
            log.warn("전송할 메시지가 없습니다.");
            return;
        }

        String destination = "/topic/" + entityType + "/" + primaryId + "/member/" + memberId;
        Map<String, Object> headers = new HashMap<>();
        headers.put("offset", messages.get(messages.size() - 1).offset());
        headers.put("type", MessageOrigin.FETCHED.toString());

        messagingTemplate.convertAndSend(destination, messages, headers);
        log.info("STOMP 메시지 전송 완료 - 전송 메시지 개수: {}", messages.size());

        messages.clear(); // 메시지 전송 후 초기화
    }

    /**
     * 순서에 맞는 ACK가 처리되었는지 확인하고, 순서가 맞지 않으면 Redis 대기 큐에 저장하여 순서를 맞춥니다.
     */
    public void processLastAcknowledgment(OffsetKey offsetKey) {
        String topic = offsetKey.topic();
        String groupId = offsetKey.groupId();
        long offset = offsetKey.offset();

        Acknowledgment acknowledgment = pendingLastAcks.get(offsetKey);

        if (acknowledgment != null) {
            // Kafka에 오프셋 커밋
            acknowledgment.acknowledge();
            pendingLastAcks.remove(offsetKey);

            // acknowledgment가 null이 아닐 때에만 Redis에 최신 오프셋 갱신
            redisService.setLastAcknowledgedOffset(topic, groupId, offset);

        } else {
            log.warn("acknowledgment가 null이므로 오프셋 갱신을 건너뜁니다 - 오프셋: {}", offset);
        }
    }


}

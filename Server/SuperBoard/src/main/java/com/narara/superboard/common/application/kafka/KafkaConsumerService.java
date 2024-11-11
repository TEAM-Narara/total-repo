package com.narara.superboard.common.application.kafka;

import com.narara.superboard.common.entity.TopicMemberOffset;
import com.narara.superboard.common.infrastructure.kafka.TopicMemberOffsetRepository;
import com.narara.superboard.common.infrastructure.redis.RedisService;
import com.narara.superboard.common.interfaces.dto.AckMessage;
import com.narara.superboard.common.interfaces.dto.LastOffsetKey;
import com.narara.superboard.common.interfaces.dto.OffsetKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 이 클래스는 실제로 Kafka 메시지를 소비하는 로직을 정의합니다.
 * KafkaListener 애노테이션을 사용하여 특정 토픽으로부터 메시지를 소비하고, 이를 비즈니스 로직으로 전달합니다.
 * 이 클래스는 메시지를 처리하고, 그에 따라 커밋 로직을 실행하는 방식으로 동작합니다.
 * 특히, 메시지 소비 후 acknowledge()를 호출하여 메시지가 정상적으로 처리되었음을 Kafka에 알리는 것이 중요합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisService redisService;
    private final TopicMemberOffsetRepository topicMemberOffsetRepository;
    private final ConsumerFactory<String, String> consumerFactory;
    // 컨슈머 리스너 중복 관리를 위한 맵
    private final Map<String, ConcurrentMessageListenerContainer<String, String>> activeListeners = new ConcurrentHashMap<>();
    private final Map<OffsetKey, Acknowledgment> pendingAcks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long WAIT_PERIOD = 3000;

    /**
     * stomp 구독 시에 자동으로 함수 호출됨
     * @param event
     */
    @EventListener
    @Transactional
    public void registerListener(SessionSubscribeEvent event) {
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
            log.info("Listener already registered for " + entityType + "Id " + primaryId + " and memberId " + memberId);
            container = activeListeners.get(listenerKey);
        } else {
            // Kafka Listener 컨테이너 팩토리 설정
            ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(createConsumerFactory(groupId));
            factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

            container = factory.createContainer(topic);

            // TODO : 컨슈머 그룹 생성할떄, topicMemberOffset 생성하기
            System.out.println("topicMemberOffset 생성하기");
            TopicMemberOffset topicMemberOffset = TopicMemberOffset.builder()
                    .topic(topic).memberId(memberId).build();
            topicMemberOffsetRepository.save(topicMemberOffset);

            container.setupMessageListener((AcknowledgingMessageListener<String, String>) (record, acknowledgment) -> {
                processRecord(record, acknowledgment, entityType, primaryId, memberId);
            });

            container.start();
            // System.out.println("11111");
            activeListeners.put(listenerKey, container);

        }

        // 구독 시점에 필요한 Offset부터 밀린 메시지 가져오기
        fetchMissedMessages(container, topic, memberId, "/topic/" + entityType + "/" + primaryId + "/member/" + memberId);
    }

    /**
     * 구독 시점에 필요한 Offset부터 밀린 메시지를 가져와 STOMP로 전송
     */
    private void fetchMissedMessages(ConcurrentMessageListenerContainer<String, String> container, String topic, Long memberId, String destination) {
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

        // ACK 대기 큐에 추가
        redisService.addAckToQueue(entityType+"-"+primaryId, "member-"+memberId, offset, record.value());

        // 오프셋 및 ACK 처리
        processAcknowledgment(new OffsetKey(record.topic(), record.partition(), offset, "member-"+memberId));
    }

    /**
     * 순서에 맞는 ACK가 처리되었는지 확인하고, 순서가 맞지 않으면 Redis 대기 큐에 저장하여 순서를 맞춥니다.
     */
    public void processAcknowledgment(OffsetKey offsetKey) {
        String topic = offsetKey.topic();
        String groupId = offsetKey.groupId();
        long offset = offsetKey.offset();
        Long memberId = Long.parseLong(groupId.replace("member-", ""));

        // Redis에서 마지막으로 ACK된 오프셋 조회
        long lastOffset = topicMemberOffsetRepository.findByTopicAndMemberId(topic,memberId)
                .map(TopicMemberOffset::getLastOffset)
                .orElse(-1L);

        Acknowledgment acknowledgment = pendingAcks.remove(offsetKey);

        if (offset == lastOffset + 1) {
            if (acknowledgment != null) {
                // Kafka에 오프셋 커밋
                acknowledgment.acknowledge();
            }

            // Redis 및 DB에 최신 오프셋 갱신
            // redisService.setLastAcknowledgedOffset(topic, groupId, offset);
            updateLastOffsetInDB(topic,memberId, offset);

            log.info("Acknowledged and updated last acknowledged offset for topic: {}, groupId: {}: {}", topic, groupId, offset);

            // 다음 오프셋을 대기 큐에서 처리
            processPendingAcks(topic, groupId, offset + 1);
        } else {
            // 순서가 맞지 않으면 Redis 대기 큐에 저장
            redisService.addAckToQueue(topic, groupId, offset, "ack_pending");
            log.warn("ACK is out of order. Expected: {}, but got: {}. Added to queue.", lastOffset + 1, offset);
            // 일정시간 기다림
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
                    updateLastOffsetInDB(topic,Long.parseLong(groupId.replace("member-", "")),nextOffset);
                    // redisService.setLastAcknowledgedOffset(topic, groupId, nextOffset); // 최신 오프셋 갱신
                    redisService.removeAckFromQueue(topic, groupId, nextOffset); // 대기 큐에서 제거
                    nextOffset++;
                }
            } else {
                break;
            }
        }
    }

    /**
     * Initiates a single wait timer for an out-of-order ACK.
     * If the ACK is not received within the WAIT_PERIOD, it logs an error and stops tracking that ACK.
     *
     * @param offsetKey the OffsetKey for which we are waiting for the ACK
     */
    private void startSingleWaitTimer(OffsetKey offsetKey) {
        scheduler.schedule(() -> {
            String pendingAck = (String) redisService.getAckFromQueue(offsetKey.topic(), offsetKey.groupId(), offsetKey.offset());
            if (pendingAck != null) {
                log.error("No ACK received for offset {} within {} ms. Stopping tracking for groupId {}.",
                        offsetKey.offset(), WAIT_PERIOD, offsetKey.groupId());
                pendingAcks.remove(offsetKey);
                redisService.removeAckFromQueue(offsetKey.topic(), offsetKey.groupId(), offsetKey.offset());
            }
        }, WAIT_PERIOD, TimeUnit.MILLISECONDS);
    }

    /**
     * offset 갱신하는 로직
     * @param topic
     * @param memberId
     * @param newOffset
     */
    private void updateLastOffsetInDB(String topic, Long memberId, Long newOffset) {
        TopicMemberOffset offsetRecord = topicMemberOffsetRepository.findByTopicAndMemberId(topic, memberId)
                .orElse(new TopicMemberOffset(topic, memberId,-1L));

        offsetRecord.updateLastOffset(newOffset);
        topicMemberOffsetRepository.save(offsetRecord);
    }

    /**
     * 엔터티별 Consumer Group을 위한 Kafka ConsumerFactory 생성
     */
    private ConsumerFactory<String, String> createConsumerFactory(String groupId) {
        Map<String, Object> props = new HashMap<>(consumerFactory.getConfigurationProperties());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    //    private void handleRecord(ConsumerRecord<String, String> record, Acknowledgment acknowledgment, String entityType, Long primaryId, Long memberId) {
//        String message = record.value();
//        String destination = "/topic/" + entityType + "/" + primaryId + "/member/" + memberId;
//        OffsetKey offsetKey = new OffsetKey(record.topic(), record.partition(), record.offset(), "member-" + memberId);
//
//        // 메시지 전송 시 헤더에 offset, partition, topic, groupId 정보 추가
//        Map<String, Object> headers = new HashMap<>();
//        headers.put("offset", record.offset());
//
//        messagingTemplate.convertAndSend(destination, message, headers);  // 헤더 포함하여 메시지 전송
//
//        pendingAcks.put(offsetKey, acknowledgment);
//        log.info("Message sent to STOMP: {} for {} {} and member {}", message, entityType, primaryId, memberId);
//    }

//    @MessageMapping("/ack")
//    public void receiveAck(@Payload AckMessage ackMessage) {
//
//        System.out.println("111111111111111111111");
//        OffsetKey offsetKey = new OffsetKey(ackMessage.topic(), ackMessage.partition()
//                , Long.parseLong(ackMessage.offset()), ackMessage.groupId());
//
//        // Log the details of the ack received
//        log.info("Received ack for topic: {}, partition: {}, offset: {}, groupId: {}",
//                ackMessage.topic(), ackMessage.partition(), ackMessage.offset(), ackMessage.groupId());
//
//        Acknowledgment acknowledgment = pendingAcks.remove(offsetKey);
//        if (acknowledgment != null) {
//            acknowledgment.acknowledge();
//            log.info("Acknowledged message at offset: {}", offsetKey.offset());
//
//            if (offsetKey.offset() == lastAcknowledgedOffset + 1) {
//                lastAcknowledgedOffset = offsetKey.offset();
//            } else {
//                log.warn("Received ack for non-contiguous offset. Last acknowledged offset: {}", lastAcknowledgedOffset);
//            }
//        } else {
//            log.warn("Acknowledgment not found for topic: {}, partition: {}, offset: {}, groupId: {}",
//                    ackMessage.topic(), ackMessage.partition(), ackMessage.offset(), ackMessage.groupId());
//        }
//    }

//    public static class OffsetKey {
//        private final String topic;
//        private final int partition;
//        private final long offset;
//
//        public OffsetKey(String topic, int partition, long offset) {
//            this.topic = topic;
//            this.partition = partition;
//            this.offset = offset;
//        }
//
//        public String getTopic() { return topic; }
//        public int getPartition() { return partition; }
//        public long getOffset() { return offset; }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (!(o instanceof OffsetKey)) return false;
//            OffsetKey that = (OffsetKey) o;
//            return partition == that.partition && offset == that.offset && topic.equals(that.topic);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(topic, partition, offset);
//        }
//    }

//    public static class AckMessage {
//        private String topic;
//        private int partition;
//        private long offset;
//
//        public String getTopic() { return topic; }
//        public void setTopic(String topic) { this.topic = topic; }
//
//        public int getPartition() { return partition; }
//        public void setPartition(int partition) { this.partition = partition; }
//
//        public long getOffset() { return offset; }
//        public void setOffset(long offset) { this.offset = offset; }
//    }

//    private final AlertService alertService;
//    private final TopicUtil topicUtil;
//
//    /**
//     * consumerRecord.value(): Kafka 토픽에서 읽어온 메시지의 실제 데이터(내용).
//     * consumerRecord.offset(): 해당 메시지가 파티션 내에서 차지하는 고유한 위치를 나타내는 오프셋 값.
//     * consumerRecord.partition(): 메시지가 저장된 Kafka 파티션 번호.
//     */
//
////    @KafkaListener(topics = "hot-area-topic", groupId = "notification-group")
//    @KafkaListener(topics = "hot-area-topic", groupId = "notification-group", containerFactory = "factory")
//    public void listenHotAreaTopic(ConsumerRecord<String, String> consumerRecord
////                                   ,@Header(KafkaHeaders.OFFSET) Long offset
//            , Acknowledgment acknowledgment // 수동 커밋 관리 위해 Acknowledgment 사용
////            , Consumer <?, ?> consumer // 자동 커밋 시 사용.
//    ) {
//        printLog(consumerRecord);
//
//        //해당 비지니스 로직 처리 후 커밋로직 작성
//        String areaName = topicUtil.extractValueByKeyFromConsumerRecord(consumerRecord, "areaName");
//        try {
//            alertService.sendHotAreaAlert(areaName);
//            //처리 후 커밋
//            acknowledgment.acknowledge();
//        } catch (FirebaseMessagingException e) {
//            throw new NotificationSendException("인기 상권 알림 전송 실패", e);
//            // 여기에 예외 처리 로직 추가 (예: 재시도, Dead Letter Queue 등)
//        }
//
//    }
//
//    //    @KafkaListener(topics = "hot-post-topic", groupId = "notification-group")
//    @KafkaListener(topics = "hot-post-topic", groupId = "notification-group", containerFactory = "factory")
//    public void listenHotPostTopic(ConsumerRecord<String, String> consumerRecord, @Header(KafkaHeaders.OFFSET) Long offset
//            , Acknowledgment acknowledgment // 수동 커밋 관리 위해 Acknowledgment 사용
//    ) {
////        printLog(consumerRecord, offset);
//
//        printLog(consumerRecord);
//        String boardName = topicUtil.extractValueByKeyFromConsumerRecord(consumerRecord, "boardName");
//        String postName = topicUtil.extractValueByKeyFromConsumerRecord(consumerRecord, "postName");
//        try {
//            alertService.sendHotPostAlert(postName, boardName);
//        } catch (FirebaseMessagingException e) {
//            throw new NotificationSendException("인기 게시글 알림 전송 실패", e);
//        }
//
//        //처리 후 커밋
//        acknowledgment.acknowledge();
//    }
//
//    //    @KafkaListener(topics = "pending-notification-topic", groupId = "notification-group")
//    @KafkaListener(topics = "pending-notification-topic", groupId = "notification-group", containerFactory = "factory")
//    public void listenPendingNotificationTopic(ConsumerRecord<String, String> consumerRecord, @Header(KafkaHeaders.OFFSET) Long offset
//            , Acknowledgment acknowledgment // 수동 커밋 관리 위해 Acknowledgment 사용
////            , Consumer <?, ?> consumer // 자동 커밋 시 사용.
//    ) {
////        printLog(consumerRecord, offset);
//        printLog(consumerRecord);
//
//        //해당 비지니스 로직 처리 후 커밋로직 작성
//        Long memberId = topicUtil.extractMemberIdFromConsumerRecord(consumerRecord);
//        try {
//            alertService.getPendingNotificationsForUser(memberId);
//        } catch (FirebaseMessagingException e) {
//            throw new NotificationSendException("대기 중이던 알림 전송 실패", e);
//        }
//
//        //처리 후 커밋
//        acknowledgment.acknowledge();
//    }

}

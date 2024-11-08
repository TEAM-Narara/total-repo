package com.narara.superboard.common.application.kafka;

import com.narara.superboard.common.enums.KafkaRegisterType;
import com.narara.superboard.common.exception.kafka.DuplicateListenerRegistrationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


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
    private final ConsumerFactory<String, String> consumerFactory;
    private final Map<String, ConcurrentMessageListenerContainer<String, String>> activeListeners
            = new ConcurrentHashMap<>();

    /**
     * 새로운 엔터티를 Kafka Consumer Group에 등록하고, 메시지를 STOMP로 전송하는 Kafka Listener 생성
     *
     * @param primaryId   주 ID (workspaceId 또는 boardId)
     * @param memberId    멤버 ID
     * @param entityType  엔터티 타입 ("workspace" 또는 "board")
     */
    public void registerListener(KafkaRegisterType entityType, Long primaryId, Long memberId) {
        String entityName = entityType.toString();
        String topic = entityName + "-" + primaryId;
        String groupId = "member-" + memberId;
        String listenerKey = topic+"-"+groupId;

        // 동일한 여러개의 리스너를 방지하기 위해 중복 체크
        if (activeListeners.containsKey(listenerKey)) {
            System.out.println("Listener already registered for " + entityName + "Id " + primaryId + " and memberId " + memberId);
            throw new DuplicateListenerRegistrationException("entityType="+entityName+", primaryId="+primaryId+
                    ", memberId="+memberId +" 의 리스너가 이미 있습니다.");
        }

        // Kafka Listener 컨테이너 팩토리 설정
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory(groupId));

        // Kafka 메시지를 수신하여 STOMP로 전송하는 메시지 리스너 설정
        ConcurrentMessageListenerContainer<String, String> container = factory.createContainer(topic);
        container.setupMessageListener((MessageListener<String, String>) record -> {
            String message = record.value();
            String destination = "/topic/" + entityName + "/" + primaryId + "/member/" + memberId;

            // STOMP로 메시지 전송
            messagingTemplate.convertAndSend(destination, message);
            System.out.println("Message sent to STOMP: " + message + " for " + entityName + " " + primaryId + " and member " + memberId + ", destination: " + destination);
        });

        // Kafka Listener 컨테이너 시작
        container.start();
        activeListeners.put(listenerKey, container);
    }

    /**TOMP 구독 이벤트 리스너: 구독 시 Kafka에서 밀린 메시지 가져와 STOMP로 전송
     */
    @EventListener
    public void handleSubscription(SessionSubscribeEvent event) {
        String destination = (String) event.getMessage().getHeaders().get("simpDestination");

        // 토픽 이름 추출
        String[] destinationParts = destination.split("/");
        if (destinationParts.length < 4) return;

        String entityType = destinationParts[2];
        Long primaryId = Long.parseLong(destinationParts[3]);
        Long memberId = Long.parseLong(destinationParts[5]);

        // Kafka에서 밀린 메시지 가져오기
//        List<String> missedMessages = getMissedMessagesForMember(entityType + "-" + primaryId,memberId);
//        for (String message : missedMessages) {
//            messagingTemplate.convertAndSend(destination, message);
//        }
    }

    /**
     * Kafka의 밀린 메시지를 특정 파티션과 Offset에서부터 가져오는 메서드
     */
//    private List<String> getMissedMessagesForMember(String topic, Long memberId) {
//        List<String> messages = new ArrayList<>();
//        Properties props = new Properties();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "member-" + memberId); // memberId 기반 그룹 ID 설정
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//
//        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
//            TopicPartition partition = new TopicPartition(topic, 0);
//            consumer.assign(Collections.singletonList(partition));
//
//            // 해당 memberId의 마지막 커밋된 Offset 위치부터 읽기 시작
//            consumer.seek(partition, consumer.position(partition));
//
//            // 메시지 읽기
//            while (true) {
//                var records = consumer.poll(Duration.ofMillis(100));
//                if (records.isEmpty()) break;
//
//                for (ConsumerRecord<String, String> record : records) {
//                    messages.add(record.value());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return messages;
//    }

    /**
     * Kafka의 밀린 메시지를 특정 파티션과 Offset에서부터 가져오는 메서드
     */
//    private List<String> getMissedMessages(String topic) {
//        List<String> messages = new ArrayList<>();
//        Properties props = new Properties();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "missed-messages-group");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//
//        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
//            TopicPartition partition = new TopicPartition(topic, 0);
//            consumer.assign(Collections.singletonList(partition));
//            consumer.seek(partition, 0); // 처음부터 읽어오기
//
//            while (true) {
//                var records = consumer.poll(Duration.ofMillis(100));
//                if (records.isEmpty()) break;
//
//                for (ConsumerRecord<String, String> record : records) {
//                    messages.add(record.value());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return messages;
//    }

    // 엔터티별 Consumer Group을 위한 Kafka ConsumerFactory 생성
    private ConsumerFactory<String, String> createConsumerFactory(String groupId) {
        // 기존 설정을 복사하여 새 설정 생성
        Map<String, Object> props = new HashMap<>(consumerFactory.getConfigurationProperties());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // 새로운 ConsumerFactory 인스턴스 생성
        return new DefaultKafkaConsumerFactory<>(props);
    }

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

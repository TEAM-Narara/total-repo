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


    @EventListener
    public void registerListener(SessionSubscribeEvent event) {
        String destination = (String) event.getMessage().getHeaders().get("simpDestination");

        System.out.println(destination);

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
            // 이미 존재하는 리스너 사용
            container = activeListeners.get(listenerKey);
        } else {
            // Kafka Listener 컨테이너 팩토리 설정
            ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(createConsumerFactory(groupId));

            // 새로운 리스너 생성 및 설정
            container = factory.createContainer(topic);
            container.setupMessageListener((MessageListener<String, String>) record -> {
                String message = record.value();
                String stompDestination = "/topic/" + entityType + "/" + primaryId + "/member/" + memberId;
                messagingTemplate.convertAndSend(stompDestination, message);
                log.info("Message sent to STOMP: " + message + " for " + entityType + " " + primaryId + " and member " + memberId);
            });

            // 리스너 시작 및 activeListeners에 등록
            container.start();
            activeListeners.put(listenerKey, container);
        }

        // 구독 시점에 필요한 Offset부터 밀린 메시지 가져오기
        fetchMissedMessages(container, topic, memberId, "/topic/" + entityType + "/" + primaryId + "/member/" + memberId);
    }

    /**
     * 구독 시점에 필요한 Offset부터 밀린 메시지를 가져와 STOMP로 전송
     */
    private void fetchMissedMessages(ConcurrentMessageListenerContainer<String, String> container, String topic, Long memberId, String destination) {
        container.stop(); // 일시적으로 Kafka 리스너 중지

        // Offset 조정 후, 메시지를 STOMP로 전송
        container.getContainerProperties().setMessageListener((MessageListener<String, String>) record -> {
            messagingTemplate.convertAndSend(destination, record.value());
            log.info("Sent missed message to STOMP: {} for member {}", record.value(), memberId);
        });

        container.start(); // 리스너 재시작하여 실시간 메시지 수신 재개
    }

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

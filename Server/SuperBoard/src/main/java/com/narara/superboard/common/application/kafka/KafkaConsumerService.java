package com.narara.superboard.common.application.kafka;

//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.joyride.alert.domain.alert.AlertService;
//import com.joyride.alert.util.exception.NotificationSendException;
//import com.joyride.alert.util.TopicUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.handler.annotation.Header;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

//import static com.joyride.alert.util.LogUtil.printLog;


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

    /**
     * 새로운 멤버를 Kafka Consumer Group에 등록하고, 메시지를 STOMP로 전송하는 Kafka Listener 생성
     *
     * @param workspaceId 워크스페이스 ID
     * @param memberId    멤버 ID
     */
    public void registerMemberListener(Long workspaceId, Long memberId) {
        String topic = "workspace-" + workspaceId;
        String groupId = "member-" + memberId;

        // Kafka Listener 컨테이너 팩토리 설정
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory(groupId));

        // Kafka 메시지를 수신하여 STOMP로 전송하는 메시지 리스너 설정
        ConcurrentMessageListenerContainer<String, String> container = factory.createContainer(topic);
        container.setupMessageListener((MessageListener<String, String>) record -> {
            String message = record.value();
            String destination = "topic/workspace/" + workspaceId + "/member/" + memberId;

            // STOMP로 메시지 전송
            messagingTemplate.convertAndSend(destination, message);
            System.out.println("Message sent to STOMP: " + message + " for member " + memberId);
        });

        // Kafka Listener 컨테이너 시작
        container.start();
    }

    // 멤버별 Consumer Group을 위한 Kafka ConsumerFactory 생성
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

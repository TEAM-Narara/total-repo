package com.narara.superboard.common.application.kafka;

//import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 처리용


    // 예시

//    public void sendHotPostMessage(String postName, String boardName) {
//        HotPostTopic payload = new HotPostTopic(boardName, postName);
//
//        try {
//            String message = objectMapper.writeValueAsString(payload); // 객체를 JSON으로 변환
//            kafkaTemplate.send("hot-board-topic", message); // Kafka 토픽으로 메시지 전송
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("메시지 변환 실패", e);
//        }
//    }
//
//    public void sendHotAreaMessage(String areaName) {
//        try {
//            String message = objectMapper.writeValueAsString(areaName); // 객체를 JSON으로 변환
//            kafkaTemplate.send("hot-area-topic", message); // Kafka 토픽으로 메시지 전송
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("메시지 변환 실패", e);
//        }
//    }
//
//    public void sendPendingMessageByMember(Long memberId) {
//        try {
//            String message = objectMapper.writeValueAsString(memberId); // 객체를 JSON으로 변환
//            kafkaTemplate.send("pending-notification-topic", message); // Kafka 토픽으로 메시지 전송
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("메시지 변환 실패", e);
//        }
//    }
}

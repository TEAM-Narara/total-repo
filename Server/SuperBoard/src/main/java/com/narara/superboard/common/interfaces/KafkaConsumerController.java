package com.narara.superboard.common.interfaces;

import com.narara.superboard.common.application.kafka.KafkaConsumerService;
import com.narara.superboard.common.application.kafka.KafkaEventListenerService;
import com.narara.superboard.common.interfaces.dto.AckMessage;
import com.narara.superboard.common.interfaces.dto.OffsetKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KafkaConsumerController {
    private final KafkaEventListenerService kafkaEventListenerService;

    /**
     * 클라이언트에서 ack를 수신하여 해당 메시지의 오프셋을 커밋
     */
    @MessageMapping("/ack")
    public void receiveAck(@Payload AckMessage ackMessage) {

        OffsetKey offsetKey = new OffsetKey(
                ackMessage.topic(),
                ackMessage.partition(),
                Long.parseLong(ackMessage.offset()),
                ackMessage.groupId()
        );

        // Log the details of the ack received
        log.info("Received ack for topic: {}, partition: {}, offset: {}, groupId: {}",
                ackMessage.topic(), ackMessage.partition(), ackMessage.offset(), ackMessage.groupId());

        kafkaEventListenerService.processAcknowledgment(offsetKey);
    }

    @MessageMapping("/ack/all")
    public void receiveAllAck(@Payload AckMessage ackMessage) {

        OffsetKey offsetKey = new OffsetKey(
                ackMessage.topic(),
                ackMessage.partition(),
                Long.parseLong(ackMessage.offset()),
                ackMessage.groupId()
        );

        log.info("AllAck 받음: {}, partition: {}, offset: {}, groupId: {}",
                ackMessage.topic(), ackMessage.partition(), ackMessage.offset(), ackMessage.groupId());

        // kafkaEventListenerService.processAcknowledgment(offsetKey);
    }
}

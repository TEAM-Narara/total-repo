package com.narara.superboard.common.interfaces;

import com.narara.superboard.common.application.kafka.KafkaConsumerService;
import com.narara.superboard.common.application.kafka.KafkaEventListenerService;
import com.narara.superboard.common.application.kafka.KafkaOffsetEventListenerService;
import com.narara.superboard.common.interfaces.dto.AckMessage;
import com.narara.superboard.common.interfaces.dto.MessageRecord;
import com.narara.superboard.common.interfaces.dto.OffsetKey;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.common.service.IAuthenticationFacade;
import com.narara.superboard.member.interfaces.dto.MemberResponseDto;
import com.narara.superboard.member.interfaces.dto.MemberUpdateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceCreateData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "0. 카프카")
@Slf4j
@Controller
@RequiredArgsConstructor
public class KafkaConsumerController {
    private final KafkaEventListenerService kafkaEventListenerService;
    private final KafkaOffsetEventListenerService kafkaOffsetEventListenerServic;
    private final IAuthenticationFacade authenticationFacade;

    /**
     * 특정 오프셋에서 데이터를 가져오는 메서드
     * @param partition   파티션 번호
     * @param offset      시작할 오프셋
     * @param entityType  엔티티 타입
     * @param primaryId   기본 ID (엔티티의 ID)
     * @return            상태 응답 메시지
     */
    @Operation(summary = "특정 오프셋에서 카프카 데이터 전송")
    @GetMapping("/api/v1/kafka/messages")
    public ResponseEntity<DefaultResponse<List<MessageRecord>>> seekToOffsetAndFetch(
            @RequestParam int partition,
            @RequestParam long offset,
            @RequestParam String entityType,
            @RequestParam Long primaryId) {

        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();
        // kafkaEventListenerService를 통해 해당 오프셋으로 이동하여 데이터 가져오기
        kafkaOffsetEventListenerServic.seekToEndAndFetch(partition, offset, entityType, primaryId, memberId);

        return new ResponseEntity<>(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.KAFKA_READ_MESSAGE_SUCCESS), HttpStatus.OK
        );
    }

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

        log.info("ACK 받음: {}, partition: {}, offset: {}, groupId: {}",
                ackMessage.topic(), ackMessage.partition(), ackMessage.offset(), ackMessage.groupId());

        kafkaEventListenerService.processAcknowledgment(offsetKey);
    }

    @MessageMapping("/ack/last")
    public void receiveAllAck(@Payload AckMessage ackMessage) {

        OffsetKey offsetKey = new OffsetKey(
                ackMessage.topic(),
                ackMessage.partition(),
                Long.parseLong(ackMessage.offset()),
                ackMessage.groupId()
        );

        log.info("LastAck 받음: {}, partition: {}, offset: {}, groupId: {}",
                ackMessage.topic(), ackMessage.partition(), ackMessage.offset(), ackMessage.groupId());

        kafkaOffsetEventListenerServic.processLastAcknowledgment(offsetKey);
    }
}

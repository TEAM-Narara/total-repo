package com.narara.superboard.reply.interfaces;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.interfaces.dto.ReplySimpleResponseDto;
import com.narara.superboard.reply.interfaces.dto.ReplyUpdateRequestDto;
import com.narara.superboard.reply.service.ReplyService;
import com.narara.superboard.websocket.enums.ReplyAction;
import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import static com.narara.superboard.websocket.enums.ReplyAction.*;

@CrossOrigin
@Controller
@RequiredArgsConstructor
@MessageMapping("/reply")
public class ReplyController implements ReplyDestination {

    private static final String REPLY = "REPLY";

    private final SimpMessagingTemplate messagingTemplate;
    private final ReplyService replyService;

    @Override
    @MessageMapping("/create")
    public WebSocketResponse createReply(@AuthenticationPrincipal Member member, @RequestBody ReplyCreateRequestDto replyCreateRequestDto) {
        Reply reply = replyService.createReply(member, replyCreateRequestDto);

        return getWebSocketResponse(reply, ADD_REPLY, "/topic/board/");
    }

    @MessageMapping("/update/{cardId}")
    public WebSocketResponse updateReply(@DestinationVariable Long cardId, @RequestBody ReplyUpdateRequestDto replyUpdateRequestDto, @AuthenticationPrincipal Member member) {
        // TODO: 멤버 권한 체크하기.
        Reply reply = replyService.updateReply(member, cardId, replyUpdateRequestDto);

        return getWebSocketResponse(reply, EDIT_REPLY, "/topic/board/");
    }


    @MessageMapping("/delete/{cardId}")
    public WebSocketResponse deleteReply(@DestinationVariable Long cardId, @AuthenticationPrincipal Member member) {
        Reply reply = replyService.deleteReply(member, cardId);

        return getWebSocketResponse(reply, DELETE_REPLY, "/topic/reply/");
    }

    private WebSocketResponse getWebSocketResponse(Reply reply, ReplyAction action, String x) {
        ReplySimpleResponseDto cardDto = ReplySimpleResponseDto.builder()
                .cardId(reply.getCard().getId())
                .replyId(reply.getId())
                .content(reply.getContent())
                .build();

        // WebSocket 응답 객체 생성
        WebSocketResponse response = WebSocketResponse.of(REPLY, action, cardDto);

        // 동적 경로로 메시지 전송
        Long boardId = reply.getCard().getList().getBoard().getId();
        messagingTemplate.convertAndSend(x + boardId, response);
        return response;
    }
}

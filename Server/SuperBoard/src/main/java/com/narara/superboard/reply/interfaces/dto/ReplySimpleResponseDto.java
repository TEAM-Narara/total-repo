package com.narara.superboard.reply.interfaces.dto;

import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketData;
import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketResponse;
import lombok.Builder;

@Builder
public record ReplySimpleResponseDto(
    Long cardId,
    Long replyId,
    String content
) implements WebSocketData {

    public static ReplySimpleResponseDto of(Reply reply){
        return ReplySimpleResponseDto.builder()
                .replyId(reply.getId())
                .cardId(reply.getCard().getId())
                .content(reply.getContent())
                .build();
    }
}

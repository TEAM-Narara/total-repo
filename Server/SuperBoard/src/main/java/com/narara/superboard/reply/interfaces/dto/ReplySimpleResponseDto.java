package com.narara.superboard.reply.interfaces.dto;

import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketData;
import lombok.Builder;

@Builder
public record ReplySimpleResponseDto(
    Long cardId,
    Long replyId,
    Long memberId,
    String content,
    boolean isDeleted,
    Long lastUpdatedAt
) implements WebSocketData {

    public static ReplySimpleResponseDto of(Reply reply){
        return ReplySimpleResponseDto.builder()
                .replyId(reply.getId())
                .cardId(reply.getCard().getId())
                .memberId(reply.getMember().getId())
                .content(reply.getContent())
                .isDeleted(reply.getIsDeleted())
                .lastUpdatedAt(reply.getUpdatedAt())
                .build();
    }
}

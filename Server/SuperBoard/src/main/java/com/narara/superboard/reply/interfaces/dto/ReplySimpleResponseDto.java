package com.narara.superboard.reply.interfaces.dto;

import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketData;
import com.narara.superboard.workspace.interfaces.dto.websocket.WebSocketResponse;
import lombok.Builder;

@Builder
public record ReplySimpleResponseDto(
    Long cardId,
    Long replyId,
    String content
) implements WebSocketData {
}

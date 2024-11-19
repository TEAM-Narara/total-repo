package com.narara.superboard.reply.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.ContentHolder;

public record ReplyCreateRequestDto(
        Long cardId,
        String content
) implements ContentHolder {
}

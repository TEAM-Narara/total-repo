package com.narara.superboard.reply.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.ContentHolder;

public record ReplyUpdateRequestDto(
        String content
) implements ContentHolder {
}

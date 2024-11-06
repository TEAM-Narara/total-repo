package com.narara.superboard.reply.interfaces.dto;

import com.narara.superboard.common.document.AdditionalDetails;

public record ReplyInfo(
        String replyContent
) implements AdditionalDetails {
}

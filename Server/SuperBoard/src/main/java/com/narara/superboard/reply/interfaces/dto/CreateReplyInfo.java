package com.narara.superboard.reply.interfaces.dto;

import com.narara.superboard.common.document.AdditionalDetails;

public record CreateReplyInfo(
        String replyContent
) implements AdditionalDetails {
}

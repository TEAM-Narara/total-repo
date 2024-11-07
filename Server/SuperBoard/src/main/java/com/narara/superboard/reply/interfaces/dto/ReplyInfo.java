package com.narara.superboard.reply.interfaces.dto;

import com.narara.superboard.common.document.Target;

public record ReplyInfo(
        Long cardId,
        String cardName,
        Long replyId,
        String replyContent
) implements Target {
}

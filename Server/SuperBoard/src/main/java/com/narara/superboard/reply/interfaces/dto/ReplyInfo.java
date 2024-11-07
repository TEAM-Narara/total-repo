package com.narara.superboard.reply.interfaces.dto;



public record ReplyInfo(
        Long cardId,
        String cardName,
        Long replyId,
        String replyContent
) {
}

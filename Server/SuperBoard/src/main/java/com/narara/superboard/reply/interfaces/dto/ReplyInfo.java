package com.narara.superboard.reply.interfaces.dto;


import com.narara.superboard.card.document.CardInfo;

public record ReplyInfo(
        Long cardId,
        String cardName,
        Long replyId,
        String replyContent
) implements CardInfo {
}

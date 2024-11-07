package com.narara.superboard.card.interfaces.dto.log;

import com.narara.superboard.common.document.Target;

// Card 삭제 관련 정보
public record DeleteCardInfo(
        Long listId,
        String listName,
        Long cardId,
        String cardName
) implements Target {
}

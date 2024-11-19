package com.narara.superboard.card.interfaces.dto.log;



// Card 삭제 관련 정보
public record DeleteCardInfo(
        Long listId,
        String listName,
        Long cardId,
        String cardName
) {
}

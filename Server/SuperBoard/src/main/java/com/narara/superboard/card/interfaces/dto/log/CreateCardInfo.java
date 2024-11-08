package com.narara.superboard.card.interfaces.dto.log;



// Card 생성 관련 정보
public record CreateCardInfo(
        Long listId,
        String listName,
        Long cardId,
        String cardName
) { }

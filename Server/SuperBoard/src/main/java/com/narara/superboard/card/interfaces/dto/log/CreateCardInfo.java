package com.narara.superboard.card.interfaces.dto.log;


import com.narara.superboard.card.document.CardInfo;

// Card 생성 관련 정보
public record CreateCardInfo(
        Long listId,
        String listName,
        Long cardId,
        String cardName
) implements CardInfo { }

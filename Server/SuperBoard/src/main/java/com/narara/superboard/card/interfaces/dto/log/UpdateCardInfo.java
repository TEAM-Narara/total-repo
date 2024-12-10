package com.narara.superboard.card.interfaces.dto.log;


import com.narara.superboard.card.document.CardInfo;

// Card 수정 관련 정보
public record UpdateCardInfo(
        Long listId,
        String listName,
        Long cardId,
        String cardName
) implements CardInfo { }

package com.narara.superboard.card.service;

import com.narara.superboard.card.interfaces.dto.CardMoveResult;
import com.narara.superboard.member.entity.Member;

public interface CardMoveService {
    CardMoveResult moveCardToTop(Member member, Long cardId, Long targetListId);

    CardMoveResult moveCardToBottom(Member member, Long cardId, Long targetListId);

    CardMoveResult moveCardBetween(Member member, Long cardId, Long previousCardId, Long nextCardId);

    CardMoveResult moveCard(Member member, Long cardId, Long listId, Long myOrder);
}

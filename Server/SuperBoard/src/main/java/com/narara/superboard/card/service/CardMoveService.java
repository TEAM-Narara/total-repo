package com.narara.superboard.card.service;

import com.narara.superboard.card.interfaces.dto.CardMoveResult;
import com.narara.superboard.member.entity.Member;

public interface CardMoveService {
    CardMoveResult moveCardToTop(Member member, Long cardId);

    CardMoveResult moveCardToBottom(Member member, Long cardId);

    CardMoveResult moveCardBetween(Member member, Long cardId, Long previousCardId, Long nextCardId);

}

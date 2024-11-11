package com.narara.superboard.card.service;

import com.narara.superboard.card.interfaces.dto.CardMoveResult;
import com.narara.superboard.member.entity.Member;

public interface CardMoveService {
    CardMoveResult moveCardToTop(Member member, Long cardId);

    CardMoveResult moveCardToBottom(Member member, Long cardId);

    CardMoveResult moveCardBetween(Member member, Long cardId, Long previousCardId, Long nextCardId);

    // 다른 리스트에 이동 메서드 추가
    CardMoveResult moveCardToOtherListTop(Member member, Long cardId, Long targetListId);

    CardMoveResult moveCardToOtherListBottom(Member member, Long cardId, Long targetListId);

    CardMoveResult moveCardBetweenInAnotherList(Member member, Long cardId, Long previousCardId, Long nextCardId);
}

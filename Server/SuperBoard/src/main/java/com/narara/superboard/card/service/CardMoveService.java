package com.narara.superboard.card.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.card.interfaces.dto.CardMoveCollectionRequest;
import com.narara.superboard.card.interfaces.dto.CardMoveResult;
import com.narara.superboard.member.entity.Member;

public interface CardMoveService {
    CardMoveResult moveCardToTop(Member member, Long cardId, Long targetListId);

    CardMoveResult moveCardToBottom(Member member, Long cardId, Long targetListId);

    CardMoveResult moveCardBetween(Member member, Long cardId, Long previousCardId, Long nextCardId);

    CardMoveResult moveCardVersion2(Member member, Long listId, CardMoveCollectionRequest cardMoveCollectionRequest)
            throws FirebaseMessagingException;
}

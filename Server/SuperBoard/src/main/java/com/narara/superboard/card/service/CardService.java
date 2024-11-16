package com.narara.superboard.card.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.interfaces.dto.CardSimpleResponseDto;
import com.narara.superboard.card.interfaces.dto.activity.CardCombinedActivityResponseDto;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.card.interfaces.dto.log.CardLogDetailResponseDto;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.websocket.constant.Action;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {
    Card createCard(Member member, CardCreateRequestDto cardCreateRequestDto) throws FirebaseMessagingException;
    Card getCard(Long cardId);
    void deleteCard(Member member, Long cardId);
    Card updateCard(Member member, Long cardId, CardUpdateRequestDto cardUpdateRequestDto)
            throws FirebaseMessagingException;
    List<Card> getArchivedCardList(Member member, Long boardId);
    void changeArchiveStatusByCard(Member member, Long cardId) throws FirebaseMessagingException;
    void checkBoardMember(Card card, Member member, Action action);
    List<CardLogDetailResponseDto> getCardActivity(Long cardId);
    CardCombinedActivityResponseDto getCardCombinedLog(Long cardId, Pageable pageable);

    List<CardSimpleResponseDto> getCardsByListId(Long listId);
}

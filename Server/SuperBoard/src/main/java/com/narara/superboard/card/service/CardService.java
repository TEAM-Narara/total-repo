package com.narara.superboard.card.service;

import com.narara.superboard.card.CardAction;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.member.entity.Member;
import java.util.List;

public interface CardService {
    Card createCard(Member member, CardCreateRequestDto cardCreateRequestDto);
    Card getCard(Long cardId);
    void deleteCard(Member member, Long cardId);
    Card updateCard(Member member, Long cardId, CardUpdateRequestDto cardUpdateRequestDto);
    List<Card> getArchivedCardList(Member member, Long boardId);
    void changeArchiveStatusByCard(Member member, Long cardId);
    void checkBoardMember(Card card, Member member, CardAction action);

}

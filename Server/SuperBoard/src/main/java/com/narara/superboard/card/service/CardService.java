package com.narara.superboard.card.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.member.entity.Member;
import java.util.List;

public interface CardService {
    Card createCard(Member member, CardCreateRequestDto cardCreateRequestDto);
    Card getCard(Long cardId);
    void deleteCard(Long cardId);
    Card updateCard(Long cardId, CardUpdateRequestDto cardUpdateRequestDto);
    List<Card> getArchivedCardList(Long boardId);
    void changeArchiveStatusByCard(Long cardId);

}

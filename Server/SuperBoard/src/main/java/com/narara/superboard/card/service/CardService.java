package com.narara.superboard.card.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.interfaces.dto.CardCombinedLogDto;
import com.narara.superboard.card.interfaces.dto.CardCombinedLogResponseDto;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.card.interfaces.dto.log.CardActivityDetailResponseDto;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.websocket.constant.Action;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {
    Card createCard(Member member, CardCreateRequestDto cardCreateRequestDto);
    Card getCard(Long cardId);
    void deleteCard(Member member, Long cardId);
    Card updateCard(Member member, Long cardId, CardUpdateRequestDto cardUpdateRequestDto);
    List<Card> getArchivedCardList(Member member, Long boardId);
    void changeArchiveStatusByCard(Member member, Long cardId);
    void checkBoardMember(Card card, Member member, Action action);
    List<CardCombinedLogDto> getCardActivity(Long cardId);
    CardCombinedLogResponseDto getCardCombinedLog(Long cardId, Pageable pageable);
}

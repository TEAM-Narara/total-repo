package com.narara.superboard.card.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.common.application.validator.CoverValidator;
import com.narara.superboard.common.application.validator.LastOrderValidator;
import com.narara.superboard.common.application.validator.NameValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final ListRepository listRepository;


    private final NameValidator nameValidator;
    private final CoverValidator coverValidator;
    private final LastOrderValidator lastOrderValidator;

    @Override
    public Card createCard(CardCreateRequestDto cardCreateRequestDto) {
        nameValidator.validateCardNameIsEmpty(cardCreateRequestDto);

        List list = listRepository.findById(cardCreateRequestDto.listId())
                .orElseThrow(() -> new NotFoundEntityException(cardCreateRequestDto.listId(), "리스트"));
        lastOrderValidator.checkValidCardLastOrder(list);

        Card card = Card.createCard(cardCreateRequestDto, list);
        return cardRepository.save(card);
    }

    @Override
    public Card getCard(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundEntityException(cardId, "카드"));
    }

    @Override
    public void deleteCard(Long cardId) {
        Card card = getCard(cardId);
        card.delete();
    }

    @Override
    public Card updateCard(Long cardId, CardUpdateRequestDto cardUpdateRequestDto) {
        Card card = getCard(cardId);

        coverValidator.validateCardCover(cardUpdateRequestDto);

        return card.updateCard(cardUpdateRequestDto);
    }

    @Override
    public java.util.List<Card> getArchivedCardList(Long boardId) {
        java.util.List<List> allListByBoard = listRepository.findAllByBoardId(boardId);
        java.util.List<Card> cardCollection = new ArrayList<>();

        for (List list : allListByBoard) {
            cardCollection.addAll(cardRepository.findAllByListAndIsArchivedTrue(list));
        }

        return cardCollection;
    }

    @Override
    public void changeArchiveStatusByCard(Long cardId) {
        Card card = getCard(cardId);
        card.changeArchiveStatus();
    }

}

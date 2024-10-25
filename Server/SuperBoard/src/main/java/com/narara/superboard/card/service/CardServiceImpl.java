package com.narara.superboard.card.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastrucuture.CardRepository;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.common.application.validator.NameValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService{

    private final CardRepository cardRepository;
    private final NameValidator nameValidator;

    @Override
    public Card creadCard(CardCreateRequestDto cardCreateRequestDto) {
        nameValidator.validateCardNameIsEmpty(cardCreateRequestDto);

        Card card = Card.createCard(cardCreateRequestDto);
        return cardRepository.save(card);
    }

}

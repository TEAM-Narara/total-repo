package com.narara.superboard.card.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;

public interface CardService {
    Card createCard(CardCreateRequestDto cardCreateRequestDto);
}

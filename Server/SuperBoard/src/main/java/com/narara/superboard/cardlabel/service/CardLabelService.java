package com.narara.superboard.cardlabel.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.cardlabel.interfaces.dto.CardLabelDto;
import com.narara.superboard.label.entity.Label;

import java.util.List;

public interface CardLabelService {
    CardLabel createCardLabel(Card card, Label label);
    CardLabel changeCardLabelIsActivated(Card card, Label label,Boolean isActivated);
    List<CardLabelDto> getCardLabelCollection(Long cardId);
}

package com.narara.superboard.cardlabel.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.cardlabel.interfaces.dto.CardLabelDto;
import com.narara.superboard.label.entity.Label;

import java.util.List;

public interface CardLabelService {
    CardLabel createCardLabel(Card card, Label label);
    CardLabel changeCardLabelIsActivated(Card card, Label label);
    List<CardLabel> getCardLabels(Card card);

    // TODO : 카드 라벨 조회 (보드의 라벨도 포함)
    List<CardLabelDto> getCardLabelsDto(Long cardId);
}

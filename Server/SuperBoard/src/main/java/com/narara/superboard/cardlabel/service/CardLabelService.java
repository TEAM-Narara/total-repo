package com.narara.superboard.cardlabel.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.label.entity.Label;

public interface CardLabelService {
    CardLabel createCardLabel(Card card, Label label);
}

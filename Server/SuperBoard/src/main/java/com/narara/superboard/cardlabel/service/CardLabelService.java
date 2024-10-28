package com.narara.superboard.cardlabel.service;

import com.narara.superboard.cardlabel.entity.CardLabel;

public interface CardLabelService {
    CardLabel createCardLabel(Long cardId, Long labelId);
}

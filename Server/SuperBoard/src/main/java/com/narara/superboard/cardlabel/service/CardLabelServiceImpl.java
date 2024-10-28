package com.narara.superboard.cardlabel.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.cardlabel.infrastructrue.CardLabelRepository;
import com.narara.superboard.cardlabel.service.validator.CardLabelValidator;
import com.narara.superboard.label.entity.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CardLabelServiceImpl implements CardLabelService {

    private final CardLabelRepository cardLabelRepository;

    private final CardLabelValidator cardLabelValidator;

    @Override
    public CardLabel createCardLabel(Card card, Label label) {
        cardLabelValidator.validateMismatchBoard(card, label);

        return cardLabelRepository.findByCardAndLabel(card, label)
                .orElseGet(() -> cardLabelRepository.save(CardLabel.createCardLabel(card, label)));
    }

}

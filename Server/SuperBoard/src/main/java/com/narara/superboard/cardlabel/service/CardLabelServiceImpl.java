package com.narara.superboard.cardlabel.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.cardlabel.infrastructrue.CardLabelRepository;
import com.narara.superboard.cardlabel.service.validator.CardLabelValidator;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.infrastructure.LabelRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CardLabelServiceImpl implements CardLabelService {

    private final CardRepository cardRepository;
    private final LabelRepository labelRepository;
    private final CardLabelRepository cardLabelRepository;

    private final CardLabelValidator cardLabelValidator;

    @Override
    public CardLabel createCardLabel(Long cardId, Long labelId) {
        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new EntityNotFoundException(labelId + "라벨"));
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException(cardId + "카드"));

        cardLabelValidator.validateMismatchBoard(card, label);

        return cardLabelRepository.findByCardAndLabel(card, label)
                .orElseGet(() -> cardLabelRepository.save(CardLabel.createCardLabel(card, label)));
    }

}

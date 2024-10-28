package com.narara.superboard.cardlabel.service;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.cardlabel.infrastructrue.CardLabelRepository;
import com.narara.superboard.cardlabel.service.validator.CardLabelValidator;
import com.narara.superboard.label.entity.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CardLabelServiceImpl implements CardLabelService {

    private final CardLabelRepository cardLabelRepository;

    private final CardLabelValidator cardLabelValidator;


    @Override
    public CardLabel changeCardLabelIsActivated(Card card, Label label) {
        Optional<CardLabel> cardLabel = cardLabelRepository.findByCardAndLabel(card, label);

        if (cardLabel.isEmpty()) {
            return createCardLabel(card, label);
        }

        return cardLabel.get().changeIsActivated();
    }

    @Override
    public CardLabel createCardLabel(Card card, Label label) {
        cardLabelValidator.validateMismatchBoard(card, label);

        Optional<CardLabel> cardLabel = cardLabelRepository.findByCardAndLabel(card, label);
        if (cardLabel.isPresent()){
            throw new EntityAlreadyExistsException("카드의 라벨");
        }

        return cardLabelRepository.save(CardLabel.createCardLabel(card, label));
    }

    @Override
    public List<CardLabel> getCardLabels(Card card) {
        return cardLabelRepository.findByCard(card);
    }
}

package com.narara.superboard.cardlabel.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.cardlabel.infrastructrue.CardLabelRepository;
import com.narara.superboard.cardlabel.interfaces.dto.CardLabelDto;
import com.narara.superboard.cardlabel.service.validator.CardLabelValidator;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.infrastructure.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CardLabelServiceImpl implements CardLabelService {

    private final CardLabelRepository cardLabelRepository;

    private final CardLabelValidator cardLabelValidator;
    private final CardRepository cardRepository;
    private final LabelRepository labelRepository;

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
    public List<CardLabelDto> getCardLabelCollection(Long cardId) {
        Board board = cardRepository.findBoardByCardId(cardId);
        if (board == null) {
            throw new IllegalStateException("ID가 " + cardId + "인 카드가 보드와 연결되지 않았습니다.");
        }

        List<Label> boardLabels = labelRepository.findAllByBoard(board);

        Set<Long> cardLabelIds = cardLabelRepository.findLabelIdsByCardId(cardId);

        return createCardLabelDtoList(boardLabels, cardLabelIds);
    }

    private List<CardLabelDto> createCardLabelDtoList(List<Label> boardLabels, Set<Long> cardLabelIds) {
        return boardLabels.stream()
                .map(label -> new CardLabelDto(
                        label.getId(),
                        label.getName(),
                        label.getColor(),
                        cardLabelIds.contains(label.getId())
                ))
                .toList();
    }
}

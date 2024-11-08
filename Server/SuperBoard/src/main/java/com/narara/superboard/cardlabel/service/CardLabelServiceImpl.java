package com.narara.superboard.cardlabel.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.card.infrastructure.CardRepository;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.cardlabel.infrastructrue.CardLabelRepository;
import com.narara.superboard.cardlabel.interfaces.dto.CardLabelDto;
import com.narara.superboard.cardlabel.service.validator.CardLabelValidator;
import com.narara.superboard.label.entity.Label;
import com.narara.superboard.label.infrastructure.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Transactional
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

        List<CardLabel> cardLabelList = cardLabelRepository.findByCardId(cardId);

        return createCardLabelDtoList(boardLabels, cardLabelList);
    }

    private List<CardLabelDto> createCardLabelDtoList(List<Label> boardLabels, List<CardLabel> cardLabelList) {
        // O(1) 검색
        Map<Long, CardLabel> cardLabelMap = cardLabelList.stream()
                .collect(Collectors.toMap(
                        cardLabel -> cardLabel.getLabel().getId(),
                        cardLabel -> cardLabel
                ));

        // boardLabels를 조회하면서, cardLabel인 경우 isActivated를 true로 설정, cardLabelId를 null이 아닌 값으로 설정
        return boardLabels.stream()
                .map(boardLabel -> {
                    CardLabel cardLabel = cardLabelMap.get(boardLabel.getId());
                    if (cardLabel != null) {
                        return CardLabelDto.of(cardLabel);
                    }
                    return CardLabelDto.builder()
                            .cardLabelId(null)
                            .labelId(boardLabel.getId())
                            .name(boardLabel.getName())
                            .color(boardLabel.getColor())
                            .IsActivated(false)
                            .build();
                })
                .collect(Collectors.toList());
    }
}

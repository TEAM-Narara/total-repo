package com.narara.superboard.cardlabel.service.validator;

import com.narara.superboard.board.exception.BoardNameNotFoundException;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.common.exception.cardlabel.MismatchedBoardException;
import com.narara.superboard.label.entity.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CardLabelValidator {

    public void validateMismatchBoard(Card card, Label label) {
        if (!card.getList().getBoard().getId().equals(label.getBoard().getId())) {
            throw new MismatchedBoardException();
        }
    }

    public void validateIsActivated(Boolean isActivated) {
        if (isActivated == null) {
            throw new NotFoundException("카드 라벨","활성화 여부");
        }
    }

}

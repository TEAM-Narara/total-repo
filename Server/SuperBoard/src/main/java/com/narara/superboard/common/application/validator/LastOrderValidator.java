package com.narara.superboard.common.application.validator;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.exception.DataNotFoundException;
import com.narara.superboard.list.entity.List;
import org.springframework.stereotype.Component;

@Component
public class LastOrderValidator {
    public void checkValidListLastOrder(Board board) {
        if (board.getLastListOrder() == null) {
            throw new DataNotFoundException("보드", "리스트의 마지막 순서");
        }
    }

    public void checkValidCardLastOrder(List list) {
        if (list.getLastCardOrder() == null) {
            throw new DataNotFoundException("리스트", "카드의 마지막 순서");
        }
    }
}

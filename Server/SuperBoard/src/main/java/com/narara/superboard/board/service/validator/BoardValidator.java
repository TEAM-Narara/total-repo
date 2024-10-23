package com.narara.superboard.board.service.validator;

import com.narara.superboard.board.interfaces.dto.BoardNameHolder;
import com.narara.superboard.common.exception.BoardNameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class BoardValidator {

    public void validateNameIsPresent(BoardNameHolder boardNameHolder) {
        // 이름이 null 또는 공백이거나 빈 문자열일 때 예외 처리
        if (boardNameHolder.name() == null || boardNameHolder.name().trim().isEmpty()) {
            throw new BoardNameNotFoundException();
        }
    }

}

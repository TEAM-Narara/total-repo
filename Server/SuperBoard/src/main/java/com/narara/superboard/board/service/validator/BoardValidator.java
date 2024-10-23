package com.narara.superboard.board.service.validator;

import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.exception.BoardInvalidVisibilityFormatException;
import com.narara.superboard.board.exception.BoardVisibilityNotFoundException;
import com.narara.superboard.board.interfaces.dto.BoardCoreHolder;
import com.narara.superboard.board.exception.BoardNameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class BoardValidator {

    public void validateNameIsPresent(BoardCoreHolder boardNameHolder) {
        if (boardNameHolder.name() == null || boardNameHolder.name().trim().isEmpty()) {
            throw new BoardNameNotFoundException();
        }
    }

    public void validateVisibilityIsPresent(BoardCoreHolder boardNameHolder) {
        if (boardNameHolder.visibility() == null || boardNameHolder.visibility().trim().isEmpty()) {
            throw new BoardVisibilityNotFoundException();
        }
    }

    public void isValidVisibility(BoardCoreHolder boardNameHolder) {
        try {
            String visibility = boardNameHolder.visibility();
            Visibility.valueOf(visibility.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BoardInvalidVisibilityFormatException();
        }
    }

}

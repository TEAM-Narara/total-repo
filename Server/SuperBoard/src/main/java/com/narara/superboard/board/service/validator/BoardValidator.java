package com.narara.superboard.board.service.validator;

import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.exception.BoardInvalidVisibilityFormatException;
import com.narara.superboard.board.exception.BoardVisibilityNotFoundException;
import com.narara.superboard.board.interfaces.dto.BoardCoreHolder;
import com.narara.superboard.board.exception.BoardNameNotFoundException;
import com.narara.superboard.common.constant.enums.CoverType;
import com.narara.superboard.common.exception.cover.InvalidCoverTypeFormatException;
import com.narara.superboard.common.interfaces.dto.CoverDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoardValidator {

    public void validateNameIsPresent(BoardCoreHolder boardNameHolder) {
        if (boardNameHolder.name() == null || boardNameHolder.name().trim().isEmpty()) {
            throw new BoardNameNotFoundException();
        }
    }

    public void validateVisibilityIsPresent(BoardCoreHolder boardCoreHolder) {
        if (boardCoreHolder.visibility() == null || boardCoreHolder.visibility().trim().isEmpty()) {
            throw new BoardVisibilityNotFoundException();
        }
    }

    public void validateVisibilityIsValid(BoardCoreHolder boardCoreHolder) {
        try {
            String visibility = boardCoreHolder.visibility();
            Visibility.fromString(visibility.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BoardInvalidVisibilityFormatException();
        }
    }

    public void validateBackgroundIsValid(BoardCoreHolder boardCoreHolder) {
        CoverDto background = boardCoreHolder.background();
        for (CoverType type : CoverType.values()) {
            if (type.toString().equals(background.type())) {
                throw new InvalidCoverTypeFormatException();
            }
        }
    }
}

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

@Component
public class BoardValidator {

    public void validateNameIsPresent(BoardCoreHolder boardNameHolder) {
        if (boardNameHolder.name() == null || boardNameHolder.name().trim().isEmpty()) {
            throw new BoardNameNotFoundException();
        }
    }

    public void validateVisibilityIsPresent(BoardCoreHolder boardCoreHolder) {
        //변경되지 않음, return
        if (boardCoreHolder.visibility() == null) {
            return;
        }

        if (boardCoreHolder.visibility().trim().isEmpty()) {
            throw new BoardVisibilityNotFoundException();
        }
    }

    public void validateVisibilityIsValid(BoardCoreHolder boardCoreHolder) {
        //변경되지 않음, return
        if (boardCoreHolder.visibility() == null) {
            return;
        }

        if (boardCoreHolder.visibility().trim().isEmpty()) {
            throw new BoardVisibilityNotFoundException();
        }

        try {
            String visibility = boardCoreHolder.visibility();
            Visibility.fromString(visibility.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BoardInvalidVisibilityFormatException();
        }
    }

    public void validateBackgroundIsValid(BoardCoreHolder boardCoreHolder) {
        CoverDto background = boardCoreHolder.cover();

        //변경되지 않음, return
        if (background == null) {
            return;
        }

        for (CoverType type : CoverType.values()) {
            if (type.toString().equals(background.type())) {
                return;
            }
        }

        throw new InvalidCoverTypeFormatException();
    }
}

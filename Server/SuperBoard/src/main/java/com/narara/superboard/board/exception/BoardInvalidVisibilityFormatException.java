package com.narara.superboard.board.exception;

import com.narara.superboard.common.exception.InvalidFormatException;

public class BoardInvalidVisibilityFormatException extends InvalidFormatException {
    public BoardInvalidVisibilityFormatException() {
        super("보드", "가시성 정보");
    }

}

package com.narara.superboard.board.exception;

import com.narara.superboard.common.exception.InvalidFormatException;

public class InvalidVisibilityFormatException extends InvalidFormatException {
    public InvalidVisibilityFormatException() {
        super("보드", "가시성 정보");
    }
}

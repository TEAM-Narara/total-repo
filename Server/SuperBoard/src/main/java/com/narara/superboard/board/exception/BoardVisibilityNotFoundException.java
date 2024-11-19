package com.narara.superboard.board.exception;

import com.narara.superboard.common.exception.NotFoundException;

public class BoardVisibilityNotFoundException extends NotFoundException {
    public BoardVisibilityNotFoundException() {
        super("보드", "가시성 정보");
    }
}

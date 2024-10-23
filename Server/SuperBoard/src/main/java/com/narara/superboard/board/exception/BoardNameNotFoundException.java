package com.narara.superboard.board.exception;

import com.narara.superboard.common.exception.NotFoundException;

public class BoardNameNotFoundException extends NotFoundException {
    public BoardNameNotFoundException() {
        super("보드", "이름");
    }
}

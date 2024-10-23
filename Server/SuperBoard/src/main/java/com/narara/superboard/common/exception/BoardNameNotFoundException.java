package com.narara.superboard.common.exception;

public class BoardNameNotFoundException extends NotFoundException {
    public BoardNameNotFoundException() {
        super("보드", "이름");
    }
}

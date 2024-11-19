package com.narara.superboard.member.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("유효하지 않은 refreshToken입니다.");
    }
}

package com.narara.superboard.common.exception;

public class TokenException extends RuntimeException {
    public static final String INVALID_TOKEN = "Invalid token";
    public static final String INVALID_JWT_SIGNATURE = "Invalid JWT signature";

    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

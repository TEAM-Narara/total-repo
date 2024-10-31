package com.narara.superboard.common.exception;

import org.springframework.security.access.AccessDeniedException;

public class CustomAccessDeniedException extends AccessDeniedException {
    public CustomAccessDeniedException() {
        super("사용자 접근 제한");
    }
}

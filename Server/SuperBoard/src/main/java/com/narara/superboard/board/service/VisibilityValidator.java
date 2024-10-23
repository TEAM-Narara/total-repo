package com.narara.superboard.board.service;

import org.springframework.stereotype.Component;

@Component
public class VisibilityValidator {

    public void validateVisibilityIsPresent(String stringVisibility) {
        if (stringVisibility == null || stringVisibility.trim().isEmpty()) {
            throw new IllegalArgumentException("Visibility 값은 null이거나 비어 있을 수 없습니다.");
        }
    }

}

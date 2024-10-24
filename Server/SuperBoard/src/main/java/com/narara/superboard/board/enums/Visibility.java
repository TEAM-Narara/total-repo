package com.narara.superboard.board.enums;

import com.narara.superboard.board.exception.BoardInvalidVisibilityFormatException;
import lombok.AllArgsConstructor;
import lombok.Getter;

public enum Visibility {
    PRIVATE,
    WORKSPACE;

    // 문자열을 받아서 Visibility enum을 반환하는 커스텀 메서드
    public static Visibility fromString(String stringVisibility) {
        try {
            // valueOf를 사용하여 매칭되는 Visibility 열거형 상수 반환
            return Visibility.valueOf(stringVisibility.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // IllegalArgumentException 대신 커스텀 예외를 던짐
            throw new BoardInvalidVisibilityFormatException();
        }
    }
}

package com.narara.superboard.board.enums;

import com.narara.superboard.board.exception.BoardInvalidVisibilityFormatException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Visibility {
    PRIVATE("private"),
    PUBLIC("workspace");

    private final String value;

    // 문자열을 받아서 Visibility enum을 반환하는 메서드
    public static Visibility fromString(String stringVisibility) {
        // 대소문자 구분 없이 입력받은 문자열과 매칭
        String normalizedInput = stringVisibility.trim().toUpperCase();

        for (Visibility visibility : Visibility.values()) {
            if (visibility.name().equalsIgnoreCase(normalizedInput) || visibility.value.equalsIgnoreCase(normalizedInput)) {
                return visibility;
            }
        }

        throw new BoardInvalidVisibilityFormatException();
    }
}

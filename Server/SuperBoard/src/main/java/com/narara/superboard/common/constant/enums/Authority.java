package com.narara.superboard.common.constant.enums;

import com.narara.superboard.common.exception.authority.InvalidAuthorityFormatException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Authority {
    ADMIN,
    MEMBER;

    // 문자열을 받아서 enum을 반환하는 커스텀 메서드
    public static Authority fromString(String stringAuthority) {
        try {
            // valueOf를 사용하여 매칭되는 Visibility 열거형 상수 반환
            return Authority.valueOf(stringAuthority.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // IllegalArgumentException 대신 커스텀 예외를 던짐
            throw new InvalidAuthorityFormatException();
        }
    }
}

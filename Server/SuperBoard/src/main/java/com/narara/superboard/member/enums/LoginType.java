package com.narara.superboard.member.enums;

import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.exception.BoardInvalidVisibilityFormatException;
import com.narara.superboard.member.exception.MemberInvalidLoginTypeFormatException;

public enum LoginType {
    LOCAL,
    NAVER,
    GITHUB;

    // 문자열을 받아서 Visibility enum을 반환하는 커스텀 메서드
    public static LoginType fromString(String stringLoginType) {
        try {
            // valueOf를 사용하여 매칭되는 LoginType 반환
            return LoginType.valueOf(stringLoginType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // IllegalArgumentException 대신 커스텀 예외를 던짐
            throw new MemberInvalidLoginTypeFormatException();
        }
    }
}

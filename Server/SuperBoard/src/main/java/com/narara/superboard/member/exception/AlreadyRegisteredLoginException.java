package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.InvalidFormatException;
import lombok.Getter;

@Getter
public class AlreadyRegisteredLoginException extends RuntimeException {
    private final String loginType;

    public AlreadyRegisteredLoginException(String email, String loginType) {
        super("이미 가입된 로그인입니다: 이메일: " + email + ", 로그인 유형: " + loginType);
        this.loginType = loginType;
    }

}

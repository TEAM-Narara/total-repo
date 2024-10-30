package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.InvalidFormatException;

public class InvalidVerificationCodeException extends InvalidFormatException {
    public InvalidVerificationCodeException(String email, String code) {
        super("이메일: " + email + "에 대한 인증 코드 '" + code + "'가 유효하지 않습니다.");
    }
}

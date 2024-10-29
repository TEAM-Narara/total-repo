package com.narara.superboard.member.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException(String email, String code) {
        super("이메일: " + email + "에 대한 인증 코드 '" + code + "'가 유효하지 않습니다.");
    }
}

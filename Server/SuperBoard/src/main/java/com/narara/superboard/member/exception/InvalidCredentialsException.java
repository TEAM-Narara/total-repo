package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.InvalidFormatException;

public class InvalidCredentialsException extends InvalidFormatException {
    public InvalidCredentialsException() {super("이메일 또는 비밀번호가 일치하지 않습니다.");}
}

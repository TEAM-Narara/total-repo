package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.InvalidFormatException;

public class MemberInvalidPasswordFormatException extends InvalidFormatException {
    public MemberInvalidPasswordFormatException() {
        super("멤버", "비밀번호");
    }
}

package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.InvalidFormatException;

public class MemberInvalidEmailFormatException extends InvalidFormatException {
    public MemberInvalidEmailFormatException() {
        super("멤버", "이메일");
    }
}

package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.InvalidFormatException;

public class MemberInvalidLoginTypeFormatException extends InvalidFormatException {
    public MemberInvalidLoginTypeFormatException() {
        super("멤버", "로그인 타입");
    }

}

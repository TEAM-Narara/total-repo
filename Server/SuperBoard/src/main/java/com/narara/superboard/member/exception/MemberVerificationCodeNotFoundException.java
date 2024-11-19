package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.NotFoundException;

public class MemberVerificationCodeNotFoundException extends NotFoundException {
    public MemberVerificationCodeNotFoundException() {
        super("멤버", "인증코드");
    }
}

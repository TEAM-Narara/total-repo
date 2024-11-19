package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.NotFoundException;

public class MemberPasswordNotFoundException extends NotFoundException {
    public MemberPasswordNotFoundException() {
        super("멤버", "비밀번호");
    }
}

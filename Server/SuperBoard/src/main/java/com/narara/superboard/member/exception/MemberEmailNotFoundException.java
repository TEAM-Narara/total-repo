package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.NotFoundException;

public class MemberEmailNotFoundException extends NotFoundException {
    public MemberEmailNotFoundException() {
        super("멤버", "이메일");
    }
}

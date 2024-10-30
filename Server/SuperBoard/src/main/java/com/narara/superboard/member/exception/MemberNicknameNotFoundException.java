package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.NotFoundException;

public class MemberNicknameNotFoundException extends NotFoundException {

    public MemberNicknameNotFoundException() {
        super("멤버", "닉네임");
    }
}

package com.narara.superboard.member.exception;

import com.narara.superboard.common.exception.NotFoundException;

public class MemberNotFoundException extends NotFoundException {
    public MemberNotFoundException(Long memberId) {
        super("ID가 " + memberId + "인 회원을 찾을 수 없습니다.");
    }
}

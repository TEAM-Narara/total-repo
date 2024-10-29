package com.narara.superboard.member.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(Long memberId) {
        super("ID가 " + memberId + "인 회원을 찾을 수 없습니다.");
    }
}

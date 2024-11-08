package com.narara.superboard.member.exception;


public class AccountDeletedException extends RuntimeException {
    public AccountDeletedException() {super("이 계정은 탈퇴한 상태입니다.");}
}

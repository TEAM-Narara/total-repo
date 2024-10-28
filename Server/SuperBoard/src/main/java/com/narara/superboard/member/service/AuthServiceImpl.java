package com.narara.superboard.member.service;

import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import com.narara.superboard.member.interfaces.dto.VerifyEmailCodeRequestDto;

public class AuthServiceImpl implements AuthService {

    @Override
    public void sendEmailVerificationCode(String email) {

    }

    @Override
    public void verifyEmailCode(VerifyEmailCodeRequestDto verifyEmailCodeRequestDto) {

    }

    @Override
    public void register(MemberCreateRequestDto memberCreateRequestDto) {
        // 1. memberCreateRequestDto valid 확인
        // 2. member에 저장
    }

    @Override
    public void login(MemberLoginRequestDto memberLoginRequestDto) {

    }

    @Override
    public void logout(Long memberId) {

    }

    @Override
    public void withdrawal(Long memberId) {

    }

    @Override
    public void refreshAccessToken(String refreshToken) {

    }
}

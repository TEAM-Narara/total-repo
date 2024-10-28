package com.narara.superboard.member.service;

import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import com.narara.superboard.member.interfaces.dto.TokenDto;
import com.narara.superboard.member.interfaces.dto.VerifyEmailCodeRequestDto;

public interface AuthService {
    // TODO : 이메일 인증코드 전송
    void sendEmailVerificationCode(String email);
    // TODO : 이메일 인증코드 인증
    void verifyEmailCode(VerifyEmailCodeRequestDto verifyEmailCodeRequestDto);
    TokenDto register(MemberCreateRequestDto memberCreateRequestDto);
    TokenDto login(MemberLoginRequestDto memberLoginRequestDto);
    // TODO : 로그아웃
    void logout(Long memberId);
    // TODO : 회원 탈퇴
    void withdrawal(Long memberId);
    // TODO : RefreshToken을 기반으로 새로운 AccessToken을 발급
    void refreshAccessToken(String refreshToken);
}

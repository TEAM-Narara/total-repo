package com.narara.superboard.member.service;

import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import com.narara.superboard.member.interfaces.dto.TokenDto;
import com.narara.superboard.member.interfaces.dto.VerifyEmailCodeRequestDto;

public interface AuthService {
    TokenDto register(MemberCreateRequestDto memberCreateRequestDto);
    TokenDto login(MemberLoginRequestDto memberLoginRequestDto);
    void logout(Long memberId);
    void withdrawal(Long memberId);
    // TODO : RefreshToken을 기반으로 새로운 AccessToken을 발급
    String reissueAccessToken(String refreshToken);
}

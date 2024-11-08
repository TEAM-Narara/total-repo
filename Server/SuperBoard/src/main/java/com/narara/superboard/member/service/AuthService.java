package com.narara.superboard.member.service;

import com.narara.superboard.member.interfaces.dto.*;

public interface AuthService {
    MemberLoginResponseDto register(MemberCreateRequestDto memberCreateRequestDto);
    MemberLoginResponseDto login(MemberLoginRequestDto memberLoginRequestDto);
    void logout(Long memberId);
    void withdrawal(Long memberId);
    String reissueAccessToken(String refreshToken);
}

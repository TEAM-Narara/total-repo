package com.narara.superboard.member.service;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.interfaces.dto.MemberLoginResponseDto;
import com.narara.superboard.member.interfaces.dto.TokenDto;

public interface OAuth2UserService {
    MemberLoginResponseDto getUserInfo(String accessToken, String provider);
}

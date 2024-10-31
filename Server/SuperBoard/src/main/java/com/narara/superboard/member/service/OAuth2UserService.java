package com.narara.superboard.member.service;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.interfaces.dto.TokenDto;

public interface OAuth2UserService {
    TokenDto getUserInfo(String accessToken, String provider);
}

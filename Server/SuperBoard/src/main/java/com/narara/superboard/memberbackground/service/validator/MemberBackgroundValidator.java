package com.narara.superboard.memberbackground.service.validator;

import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.member.exception.*;
import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import com.narara.superboard.member.interfaces.dto.VerifyEmailCodeRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MemberBackgroundValidator {

    // 이미지 url 검증
    public void validateImgUrl(String imgUrl) {
        if(!StringUtils.hasText(imgUrl)) {
            throw new NotFoundException("멤버의 배경","imgUrl");
        }
    }
}

package com.narara.superboard.fcmtoken.interfaces.dto;

import com.narara.superboard.fcmtoken.entity.FcmToken;
import lombok.Builder;

@Builder
public record FcmTokenResponseDto(
        String registrationToken
) {
    public static FcmTokenResponseDto of(FcmToken fcmToken){
        return FcmTokenResponseDto.builder()
                .registrationToken(fcmToken.getRegistrationToken())
                .build();
    }
}

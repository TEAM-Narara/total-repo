package com.narara.superboard.fcmtoken.service;

import com.narara.superboard.fcmtoken.entity.FcmToken;

public interface FcmTokenService {
    // TODO : fcm 토큰 생성
    FcmToken createFcmToken(Long memberId,String registrationToken);
    // TODO : fcm 토큰 수정
    FcmToken updateFcmToken(Long memberId,String registrationToken);
    // TODO : fcm 토큰 삭제
    void deleteFcmToken(Long memberId);
}

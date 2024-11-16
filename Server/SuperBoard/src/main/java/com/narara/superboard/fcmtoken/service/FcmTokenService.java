package com.narara.superboard.fcmtoken.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.fcmtoken.entity.FcmToken;
import com.narara.superboard.member.entity.Member;
import java.util.Map;

public interface FcmTokenService {
    FcmToken createFcmToken(Long memberId, String registrationToken);

    FcmToken updateFcmToken(Long memberId, String registrationToken);

    void deleteFcmTokenByMember(Member member);

    void sendMessage(Member member, String title, String body, Map<String, String> data)
            throws FirebaseMessagingException;
}

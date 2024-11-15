package com.narara.superboard.fcmtoken.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.fcmtoken.entity.FcmToken;
import com.narara.superboard.fcmtoken.infrastructure.FcmTokenRepository;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenServiceImpl implements FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    public FcmToken createFcmToken(Long memberId, String registrationToken) {
        Member member = validateMemberExists(memberId);
        validateRegistrationToken(registrationToken);

        FcmToken fcmToken = FcmToken.builder()
                .member(member)
                .registrationToken(registrationToken)
                .build();

        return fcmTokenRepository.save(fcmToken);
    }

    @Override
    public FcmToken updateFcmToken(Long memberId, String registrationToken) {
        validateMemberExists(memberId);
        validateRegistrationToken(registrationToken);

        FcmToken fcmToken = fcmTokenRepository.findFirstByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException("fcm토큰", "토큰"));

        fcmToken.changeRegistrationToken(registrationToken);

        return fcmTokenRepository.save(fcmToken);
    }

    @Override
    public void deleteFcmTokenByMember(Member member) {
        List<FcmToken> fcmTokenList = fcmTokenRepository.findAllByMember(member);
        fcmTokenRepository.deleteAll(fcmTokenList);
    }

    @Override
    public void sendMessage(Member member, String title, String body) throws FirebaseMessagingException {
        FcmToken fcmToken = fcmTokenRepository.findFirstByMemberId(member.getId())
                .orElseThrow(() -> new NotFoundException("fcm토큰", "토큰"));

        String message = FirebaseMessaging.getInstance().send(Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(fcmToken.getRegistrationToken())  // 대상 디바이스의 등록 토큰
                .build());
    }

    private Member validateMemberExists(Long memberId) {
        return memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new NotFoundEntityException(memberId, "멤버"));
    }

    private void validateRegistrationToken(String registrationToken) {
        if (registrationToken == null || registrationToken.isEmpty()) {
            throw new NotFoundException("fcm토큰", "토큰");
        }
    }
}

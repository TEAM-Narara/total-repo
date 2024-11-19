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
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenServiceImpl implements FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    public FcmToken createFcmToken(Long memberId, String registrationToken) {
        Member member = validateMemberExists(memberId);
        validateRegistrationToken(registrationToken);

        FcmToken fcmToken = fcmTokenRepository.findFirstByMemberAndRegistrationToken(member, registrationToken)
                .orElseGet(() -> null);

        if (fcmToken == null) {
            fcmToken = FcmToken.builder()
                    .member(member)
                    .registrationToken(registrationToken)
                    .build();

            fcmToken = fcmTokenRepository.save(fcmToken);
        }

        return fcmToken;
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
    public void sendMessage(Member member, String title, String body, Map<String, String> data) throws FirebaseMessagingException {
        FcmToken fcmToken;
        try {
            //토큰 없다고 완전 롤백되는 건 좀 그렇다 TODO
            fcmToken = getFcmToken(member);
        } catch(NotFoundException e) {
            log.info(member.getNickname() + "님(" + member.getId() + ")의 fcm 토큰이 없습니다");
            return;
        }
//        String message = FirebaseMessaging.getInstance().send(Message.builder()
//                .setNotification(Notification.builder()
//                        .setTitle(title)
//                        .setBody(body)
//                        .build())
//                .setToken(fcmToken.getRegistrationToken())  // 대상 디바이스의 등록 토큰
//                .build());

        Message.Builder messageBuilder = Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(title) // 동적으로 생성된 제목
                                .setBody(body)   // body는 공백일 수 있음
                                .build()
                )
                .setToken(fcmToken.getRegistrationToken()); // 대상 디바이스의 FCM 토큰

        // Map 데이터를 Data 페이로드로 추가
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                messageBuilder.putData(entry.getKey(), entry.getValue());
            }
        }

        // 메시지 전송
        String messageId = FirebaseMessaging.getInstance().send(messageBuilder.build());
        System.out.println("Message sent with ID: " + messageId);
    }

    private FcmToken getFcmToken(Member member) {
        return fcmTokenRepository.findFirstByMemberId(member.getId())
                .orElseThrow(() -> new NotFoundException("fcm토큰", "토큰"));
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

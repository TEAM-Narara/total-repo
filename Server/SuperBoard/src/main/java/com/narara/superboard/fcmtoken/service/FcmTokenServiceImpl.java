package com.narara.superboard.fcmtoken.service;

import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.fcmtoken.entity.FcmToken;
import com.narara.superboard.fcmtoken.infrastructure.FcmTokenRepository;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

        FcmToken fcmToken = fcmTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException("fcm토큰", "토큰"));

        fcmToken.changeRegistrationToken(registrationToken);

        return fcmTokenRepository.save(fcmToken);
    }

    @Override
    public void deleteFcmToken(Long memberId) {
        validateMemberExists(memberId);

        FcmToken fcmToken = fcmTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException("FcmToken", "토큰"));

        fcmTokenRepository.delete(fcmToken);
    }


    private Member validateMemberExists(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundEntityException(memberId, "멤버"));
    }

    private void validateRegistrationToken(String registrationToken) {
        if (registrationToken == null || registrationToken.isEmpty()) {
            throw new NotFoundException("fcm토큰", "토큰");
        }
    }
}

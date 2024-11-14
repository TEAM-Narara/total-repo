package com.narara.superboard.fcmtoken.infrastructure;

import com.narara.superboard.fcmtoken.entity.FcmToken;
import com.narara.superboard.member.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken,Long> {
    Optional<FcmToken> findByMemberId(Long memberId);

    List<FcmToken> findAllByMember(Member member);
    Optional<FcmToken> findByRegistrationToken(String registrationToken);
}

package com.narara.superboard.fcmtoken.infrastructure;

import com.narara.superboard.fcmtoken.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken,Long> {
    Optional<FcmToken> findByMemberId(Long memberId);
}

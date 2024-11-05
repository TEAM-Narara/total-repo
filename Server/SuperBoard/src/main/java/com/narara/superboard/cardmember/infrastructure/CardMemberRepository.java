package com.narara.superboard.cardmember.infrastructure;

import com.narara.superboard.cardmember.entity.CardMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardMemberRepository extends JpaRepository<CardMember, Long> {
    Optional<CardMember> findByCardIdAndMemberId(Long cardId, Long memberId);

}

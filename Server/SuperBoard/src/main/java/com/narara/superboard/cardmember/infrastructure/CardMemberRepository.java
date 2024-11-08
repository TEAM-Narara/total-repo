package com.narara.superboard.cardmember.infrastructure;

import aj.org.objectweb.asm.commons.Remapper;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardMemberRepository extends JpaRepository<CardMember, Long> {
    Optional<CardMember> findByCardIdAndMemberId(Long cardId, Long memberId);

    Optional<CardMember> findByCardIdAndMember(Long cardId, Member member);
}

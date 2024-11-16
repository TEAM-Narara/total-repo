package com.narara.superboard.cardmember.infrastructure;

import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.member.entity.Member;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardMemberRepository extends JpaRepository<CardMember, Long> {
    Optional<CardMember> findByCardIdAndMemberId(Long cardId, Long memberId);

    Optional<CardMember> findByCardIdAndMember(Long cardId, Member member);

    @Query("select cm.member from CardMember cm " +
            "where cm.card.id = :cardId and cm.isRepresentative = true and cm.isAlert = true")
    Set<Member> findAllMemberByCardAndWatchTrue(@Param("cardId") Long cardId);
}

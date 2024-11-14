package com.narara.superboard.memberbackground.infrastructure;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.memberbackground.entity.MemberBackground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberBackgroundRepository extends JpaRepository<MemberBackground, Long> {
    List<MemberBackground> findAllByMemberId(Long memberId);
    Optional<MemberBackground> findByIdAndMemberId(Long backgroundId, Long memberId);

    List<MemberBackground> findAllByMember(Member member);

    Optional<MemberBackground> findByIdAndMember(Long backgroundId, Member member);
}

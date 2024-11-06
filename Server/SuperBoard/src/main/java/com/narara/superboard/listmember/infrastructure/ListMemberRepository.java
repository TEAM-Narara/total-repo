package com.narara.superboard.listmember.infrastructure;

import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.listmember.entity.ListMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListMemberRepository extends JpaRepository<ListMember,Long> {
    Optional<ListMember> findByListIdAndMemberId(Long listId, Long memberId);
}

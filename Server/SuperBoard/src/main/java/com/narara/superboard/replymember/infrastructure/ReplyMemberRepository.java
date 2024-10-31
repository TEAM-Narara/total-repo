package com.narara.superboard.replymember.infrastructure;

import com.narara.superboard.replymember.entity.ReplyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyMemberRepository extends JpaRepository<ReplyMember, Long> {
}

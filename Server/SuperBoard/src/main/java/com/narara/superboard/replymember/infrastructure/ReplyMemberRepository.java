package com.narara.superboard.replymember.infrastructure;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import com.narara.superboard.replymember.entity.ReplyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyMemberRepository extends JpaRepository<ReplyMember, Long> {
    Boolean existsByMemberAndReply(Member member, Reply reply);
}

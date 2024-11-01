package com.narara.superboard.reply.infrastructure;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findAllByCard(Card card);
    Boolean existsByMemberAndId(Member member, Long id);
}

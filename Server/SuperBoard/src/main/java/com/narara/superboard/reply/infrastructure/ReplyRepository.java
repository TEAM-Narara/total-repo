package com.narara.superboard.reply.infrastructure;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.entity.Reply;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findAllByCard(Card card);
    Boolean existsByMemberAndId(Member member, Long id);
    @Query("SELECT r FROM Reply r WHERE r.card.list.board.id = :boardId AND r.isDeleted = false")
    Page<Reply> findAllByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    @Query("SELECT r FROM Reply r WHERE r.card.id = :cardId AND r.isDeleted = false")
    Page<Reply> findAllByCardId(@Param("cardId") Long cardId, Pageable pageable);

    Optional<Reply> findByIdAndIsDeletedFalse(Long replyId);
}

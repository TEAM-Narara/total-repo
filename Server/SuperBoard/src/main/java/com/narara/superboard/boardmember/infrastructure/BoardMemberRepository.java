package com.narara.superboard.boardmember.infrastructure;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.boardmember.entity.BoardMember;
import java.util.List;
import java.util.Optional;

import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardMemberRepository extends JpaRepository<BoardMember,Long> {
    boolean existsByMemberAndBoardAndAuthority(Member member, Board board, Authority authority);
    List<BoardMember> findAllByBoardId(Long boardId);
    Optional<BoardMember> findByBoardIdAndMember(Long boardId, Member member);
    Optional<BoardMember> findFirstByBoard_IdAndMember_Id(Long boardId, Long memberId);

    @Query("select bm from BoardMember bm join bm.board b where bm.member.id = :memberId order by b.id")
    List<BoardMember> findByMemberId(@Param("memberId") Long memberId);
}
package com.narara.superboard.boardmember.infrastructure;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardMemberRepository extends JpaRepository<BoardMember,Long> {
    boolean existsByMemberAndBoardAndAuthority(Member member, Board board, String authority);
}

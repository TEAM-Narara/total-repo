package com.narara.superboard.boardmember.infrastructure;

import com.narara.superboard.boardmember.entity.BoardMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {
    List<BoardMember> findAllByBoardId(Long boardId);
}

package com.narara.superboard.board.infrastructure;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByWorkSpaceId(Long workSpaceId); // workSpaceId
    List<Board> findAllByWorkSpaceIdAndIsArchivedTrue(Long workSpaceId);
    Optional<Board> findByIdAndIsDeletedFalse(Long boardId);
}

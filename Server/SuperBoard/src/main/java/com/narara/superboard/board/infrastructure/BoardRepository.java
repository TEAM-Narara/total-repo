package com.narara.superboard.board.infrastructure;

import com.narara.superboard.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByWorkSpaceId(Long workspaceId); // workspaceId
    List<Board> findAllByWorkSpaceIdAndIsArchivedTrueAndIsDeletedFalse(Long workspaceId);
    Optional<Board> findByIdAndIsDeletedFalse(Long boardId);
}

package com.narara.superboard.list.infrastructure;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.list.entity.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListRepository extends JpaRepository<List, Long> {
    java.util.List<List> findByBoardAndIsArchivedAndIsDeletedFalse(Board board, Boolean isArchived);

    java.util.List<List> findAllByBoard(Board board);
    java.util.List<List> findAllByBoardId(Long boardId);
}

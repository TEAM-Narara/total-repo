package com.narara.superboard.list.infrastructure;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.list.entity.List;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ListRepository extends JpaRepository<List, Long> {
    java.util.List<List> findByBoardAndIsArchivedAndIsDeletedFalse(Board board, Boolean isArchived);

    java.util.List<List> findAllByBoard(Board board);
    java.util.List<List> findAllByBoardId(Long boardId);

    Optional<List> findFirstByBoardOrderByMyOrderAsc(Board board);
    Optional<List> findFirstByBoardOrderByMyOrderDesc(Board board);

    Boolean existsByBoardAndMyOrder(Board board, Long myOrder);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    java.util.List<List> findAllByBoardOrderByMyOrderAsc(Board board);

    java.util.List<List> findByBoardAndIsDeletedFalseOrderByMyOrderAsc(Board board);
}

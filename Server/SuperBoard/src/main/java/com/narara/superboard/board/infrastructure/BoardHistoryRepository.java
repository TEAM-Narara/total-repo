package com.narara.superboard.board.infrastructure;

import com.narara.superboard.board.document.BoardHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BoardHistoryRepository extends MongoRepository<BoardHistory, String> {
    List<BoardHistory> findByWhere_BoardIdOrderByWhenDesc(Long boardId);
    Page<BoardHistory> findByWhere_BoardIdOrderByWhenDesc(Long boardId, Pageable pageable);
}


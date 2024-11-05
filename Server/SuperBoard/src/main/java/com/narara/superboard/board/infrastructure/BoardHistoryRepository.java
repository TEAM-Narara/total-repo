package com.narara.superboard.board.infrastructure;

import com.narara.superboard.board.document.BoardHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardHistoryRepository extends MongoRepository<BoardHistory, String> {
}

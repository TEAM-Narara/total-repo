package com.narara.superboard.card.infrastructure;

import com.narara.superboard.card.document.CardHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CardHistoryRepository extends MongoRepository<CardHistory, String> {
    List<CardHistory> findByWhere_BoardIdOrderByWhenDesc(Long boardId);
}

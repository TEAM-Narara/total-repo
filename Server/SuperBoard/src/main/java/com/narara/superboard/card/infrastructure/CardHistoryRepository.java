package com.narara.superboard.card.infrastructure;

import com.narara.superboard.card.document.CardHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CardHistoryRepository extends MongoRepository<CardHistory, String> {
}

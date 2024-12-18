package com.narara.superboard.card.infrastructure;

import com.narara.superboard.board.document.BoardHistory;
import com.narara.superboard.card.document.CardHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import org.springframework.data.mongodb.repository.Query;

public interface CardHistoryRepository extends MongoRepository<CardHistory, String> {
    List<CardHistory> findByWhere_BoardIdOrderByWhenDesc(Long boardId);
    Page<CardHistory> findByWhere_BoardIdOrderByWhenDesc(Long boardId, Pageable pageable);

//    Page<CardHistory> findByWhere_BoardIdInOrderByWhenDesc(Long boardId, Pageable pageable);

    List<CardHistory> findByWhere_CardIdOrderByWhenDesc(Long cardId);
    Page<CardHistory> findByWhere_CardIdOrderByWhenDesc(Long cardId, Pageable pageable);

    @Query("{ 'where.cardId': ?0, 'eventData': { $ne: ['COMMENT', 'ATTACHMENT'] } }")
    Page<CardHistory> findByWhere_CardIdAndEventDataNotInOrderByWhenDesc(Long cardId, Pageable pageable);

    @Query("{ 'where.cardId': ?0, 'eventData': { $eq: 'ATTACHMENT' } }")
    Page<CardHistory> findByWhere_CardIdAndEventDataInAttachmentOrderByWhenDesc(Long cardId, Pageable pageable);
}

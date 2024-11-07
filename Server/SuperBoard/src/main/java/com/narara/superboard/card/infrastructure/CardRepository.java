package com.narara.superboard.card.infrastructure;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.list.entity.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    java.util.List<Card> findAllByListAndIsArchivedTrue(List list);

    // cardId로 보드 엔티티 전체 조회
    @Query("SELECT c.list.board FROM Card c WHERE c.id = :cardId")
    Board findBoardByCardId(@Param("cardId") Long cardId);

    java.util.List<Card> findAllByList(List list);

    Optional<Card> findByIdAndIsDeletedFalse(Long cardId);
}

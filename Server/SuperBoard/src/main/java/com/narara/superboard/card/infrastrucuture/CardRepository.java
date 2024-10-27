package com.narara.superboard.card.infrastrucuture;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.card.entity.Card;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByBoardIdAndIsArchivedTrue(Long boardId);
}

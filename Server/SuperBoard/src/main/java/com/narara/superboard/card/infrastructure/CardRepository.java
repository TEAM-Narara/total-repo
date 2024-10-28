package com.narara.superboard.card.infrastructure;

import com.narara.superboard.card.entity.Card;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByListAndIsArchivedTrue(com.narara.superboard.list.entity.List list);
}

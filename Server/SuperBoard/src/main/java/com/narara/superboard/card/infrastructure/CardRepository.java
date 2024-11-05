package com.narara.superboard.card.infrastructure;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.list.entity.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    java.util.List<Card> findAllByListAndIsArchivedTrue(List list);
    java.util.List<Card> findAllByList(List list);
}

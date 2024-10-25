package com.narara.superboard.card.infrastrucuture;

import com.narara.superboard.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}

package com.narara.superboard.cardlabel.infrastructrue;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.label.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardLabelRepository extends JpaRepository<CardLabel, Long> {
    Optional<CardLabel> findByCardAndLabel(Card card, Label label);

    List<CardLabel> findByCard(Card card);

    @Query("select cl from CardLabel cl WHERE cl.card.id = :cardId AND cl.isActivated = true")
    List<CardLabel> findByCardId(@Param("cardId") Long cardId);
}

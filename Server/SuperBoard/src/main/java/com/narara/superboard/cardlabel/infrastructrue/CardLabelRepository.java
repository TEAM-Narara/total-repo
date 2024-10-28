package com.narara.superboard.cardlabel.infrastructrue;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.label.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardLabelRepository extends JpaRepository<CardLabel, Long> {
    Optional<CardLabel> findByCardAndLabel(Card card, Label label);
}

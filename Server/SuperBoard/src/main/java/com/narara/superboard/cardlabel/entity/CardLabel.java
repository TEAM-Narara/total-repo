package com.narara.superboard.cardlabel.entity;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.label.entity.Label;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "card_label")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardLabel extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "label_id", nullable = false)
    private Label label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "is_activated", nullable = false, columnDefinition = "boolean default true")
    private Boolean isActivated;

    public static CardLabel createCardLabel(Card card, Label label) {
        return CardLabel.builder()
                .card(card)
                .label(label)
                .isActivated(true)
                .build();
    }

    public CardLabel changeIsActivated(){
        this.isActivated = !this.isActivated;
        return this;
    }
}
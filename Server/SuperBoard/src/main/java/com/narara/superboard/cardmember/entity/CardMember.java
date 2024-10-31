package com.narara.superboard.cardmember.entity;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "card_member")
public class CardMember extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키

    @JoinColumn(name = "member", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;  // 멤버 ID

    @JoinColumn(name = "card", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;  // 워크스페이스 ID

    @Column(name = "is_alert", nullable = false, columnDefinition = "boolean default false")
    private boolean isAlert;

    public static CardMember createCardMember(Card card, Member member){
        return CardMember.builder()
                .member(member)
                .card(card)
                .build();
    }

    public CardMember(Card card) {
        this.card = card;
    }

    public CardMember(Member member) {
        this.member = member;
    }

    public void changeIsAlert() {
        this.isAlert = !isAlert;
    }
}

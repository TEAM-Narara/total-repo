package com.narara.superboard.cardmember.entity;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
public class CardMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키

    @JoinColumn(name = "member", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;  // 멤버 ID

    @JoinColumn(name = "card", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;  // 워크스페이스 ID

    @Column(name = "is_alert", nullable = false, columnDefinition = "boolean default false")
    private boolean isAlert;

    public CardMember createCardMember(Card card, Member member){
        this.member = member;
        this.card = card;
        return this;
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

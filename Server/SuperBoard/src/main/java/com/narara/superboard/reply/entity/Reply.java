package com.narara.superboard.reply.entity;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
import com.narara.superboard.reply.interfaces.dto.ReplyUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reply")
@Entity
@Builder
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본키

    @JoinColumn(name = "card_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;  // 보드 키

    @Column(name = "content", nullable = false)
    private String content;  // 이름

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    public static Reply createReply(ReplyCreateRequestDto replyCreateRequestDto, Card card) {
        return Reply.builder()
                .content(replyCreateRequestDto.content())
                .card(card)
                .build();
    }

    public Reply updateReply(ReplyUpdateRequestDto replyUpdateRequestDto) {
        this.content = replyUpdateRequestDto.content();
        return this;
    }

    public Reply deleteReply() {
        this.isDeleted = true;
        return this;
    }
}

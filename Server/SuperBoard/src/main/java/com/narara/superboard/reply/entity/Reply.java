package com.narara.superboard.reply.entity;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.reply.interfaces.dto.ReplyCreateRequestDto;
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
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reply")
@Entity
@Builder
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본키

    @JoinColumn(name = "board_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;  // 보드 키

    @Column(name = "content", nullable = false)
    private String content;  // 이름

    public static Reply createReply(ReplyCreateRequestDto replyCreateRequestDto, Card card) {
        return Reply.builder()
                .content(replyCreateRequestDto.content())
                .card(card)
                .build();
    }
}

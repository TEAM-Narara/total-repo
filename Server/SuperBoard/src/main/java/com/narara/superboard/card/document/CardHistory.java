package com.narara.superboard.card.document;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;

import com.narara.superboard.common.document.Who;
import com.narara.superboard.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "card_historys")
public class CardHistory<T>{
    @Id
    private String id;
    private Who who; // 누가
    private Long when; // 언제
    private CardWhere where; // 어디서
    private EventType eventType; // 이벤트 유형 (CREATE, UPDATE, DELETE 등) // 무엇을
    private EventData eventData; // 데이터 유형 (CARD, BOARD, LABEL 등) // 어떻게
    private T target; // 기타 등등...

    public static <T> CardHistory<T> careateCardHistory(Member member, Long when, Board board, Card card, EventType eventType, EventData eventData, T target) {
        return CardHistory.<T>builder()
                .who(Who.of(member))
                .when(when)
                .where(CardWhere.of(board, card))
                .eventType(eventType)
                .eventData(eventData)
                .target(target)
                .build();
    }
}

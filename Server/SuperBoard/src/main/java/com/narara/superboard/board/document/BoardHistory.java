package com.narara.superboard.board.document;

import com.narara.superboard.board.entity.Board;
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
@Document(collection = "board_historys")
public class BoardHistory<T> {

    @Id
    private String id;
    private Who who; // 누가
    private Long when; // 언제
    private BoardWhere where; // 어디서
    private EventType eventType; // 이벤트 유형 (CREATE, UPDATE, DELETE 등) // 무엇을
    private EventData eventData; // 데이터 유형 (CARD, BOARD, LABEL 등) // 어떻게
    private T target; // 기타 등등...

    public static <T> BoardHistory<T> createBoardHistory(Member member, Long when, Board board, EventType eventType, EventData eventData, T target) {
        return BoardHistory.<T>builder()
                .who(Who.of(member))
                .when(when)
                .where(BoardWhere.of(board))
                .eventType(eventType)
                .eventData(eventData)
                .target(target)
                .build();
    }
}

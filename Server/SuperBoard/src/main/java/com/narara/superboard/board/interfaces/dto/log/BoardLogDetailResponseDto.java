package com.narara.superboard.board.interfaces.dto.log;

import com.narara.superboard.board.document.BoardHistory;
import com.narara.superboard.board.document.BoardInfo;
import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.card.document.CardInfo;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.document.Who;

public record BoardLogDetailResponseDto<T>(
        Who who,
        Long when,
        BoardWhereDto where,
        EventType eventType,
        EventData eventData,
        T target
) {
    public static <T extends BoardInfo> BoardLogDetailResponseDto<T> createLogDetailResponseDto(
            BoardHistory<T> boardHistory
    ) {
        return new BoardLogDetailResponseDto<>(
                boardHistory.getWho(),
                boardHistory.getWhen(),
                new BoardWhereDto(boardHistory.getWhere().boardId(), boardHistory.getWhere().boardName()),
                boardHistory.getEventType(),
                boardHistory.getEventData(),
                boardHistory.getTarget()
        );
    }


    public static<T extends CardInfo> BoardLogDetailResponseDto<T> createLogDetailResponseDto(
            CardHistory<T> cardHistory
    ) {
        return new BoardLogDetailResponseDto<>(
                cardHistory.getWho(),
                cardHistory.getWhen(),
                new BoardWhereDto(cardHistory.getWhere().boardId(), cardHistory.getWhere().boardName()),
                cardHistory.getEventType(),
                cardHistory.getEventData(),
                cardHistory.getTarget()
        );
    }

}

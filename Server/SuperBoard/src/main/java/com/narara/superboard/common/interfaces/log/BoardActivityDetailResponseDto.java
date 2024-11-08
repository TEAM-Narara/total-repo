package com.narara.superboard.common.interfaces.log;

import com.narara.superboard.board.document.BoardHistory;
import com.narara.superboard.board.interfaces.dto.BoardCombinedLogResponseDto;
import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.document.Who;

public record BoardActivityDetailResponseDto<T>(
        Who who,
        Long when,
        BoardWhereDto where,
        EventType eventType,
        EventData eventData,
        T target
) {
    public static <T> BoardActivityDetailResponseDto<T> createActivityDetailResponseDto(
            BoardHistory<T> boardHistory
    ) {
        return new BoardActivityDetailResponseDto<>(
                boardHistory.getWho(),
                boardHistory.getWhen(),
                new BoardWhereDto(boardHistory.getWhere().boardId(), boardHistory.getWhere().boardName()),
                boardHistory.getEventType(),
                boardHistory.getEventData(),
                boardHistory.getTarget()
        );
    }


    public static<T> BoardActivityDetailResponseDto<T> createActivityDetailResponseDto(
            CardHistory<T> cardHistory
    ) {
        return new BoardActivityDetailResponseDto<>(
                cardHistory.getWho(),
                cardHistory.getWhen(),
                new BoardWhereDto(cardHistory.getWhere().boardId(), cardHistory.getWhere().boardName()),
                cardHistory.getEventType(),
                cardHistory.getEventData(),
                cardHistory.getTarget()
        );
    }

}

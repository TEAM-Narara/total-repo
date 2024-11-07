package com.narara.superboard.common.interfaces.log;

import com.narara.superboard.board.document.BoardHistory;
import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.document.Target;
import com.narara.superboard.common.document.Where;
import com.narara.superboard.common.document.Who;

public record ActivityDetailResponseDto(
    Who who,
    Long when,
    Where where,
    EventType eventType,
    EventData eventData,
    Target target
) {

    public static ActivityDetailResponseDto createActivityDetailResponseDto(
        BoardHistory boardHistory
    ) {
        return new ActivityDetailResponseDto(
            boardHistory.getWho(),
            boardHistory.getWhen(),
            boardHistory.getWhere(),
            boardHistory.getEventType(),
            boardHistory.getEventData(),
            boardHistory.getTarget()
        );
    }
    public static ActivityDetailResponseDto createActivityDetailResponseDto(
        CardHistory cardHistory
    ) {
        return new ActivityDetailResponseDto(
            cardHistory.getWho(),
            cardHistory.getWhen(),
            cardHistory.getWhere(),
            cardHistory.getEventType(),
            cardHistory.getEventData(),
            cardHistory.getTarget()
        );
    }

}

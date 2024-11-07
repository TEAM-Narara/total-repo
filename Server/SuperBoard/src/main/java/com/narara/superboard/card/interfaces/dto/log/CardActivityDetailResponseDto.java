package com.narara.superboard.card.interfaces.dto.log;

import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.document.Target;
import com.narara.superboard.common.document.Who;

public record CardActivityDetailResponseDto(
        Who who,
        Long when,
        CardWhereDto where,
        EventType eventType,
        EventData eventData,
        Target target
) {
    public static CardActivityDetailResponseDto createActivityDetailResponseDto(
            CardHistory cardHistory
    ) {
        return new CardActivityDetailResponseDto(
                cardHistory.getWho(),
                cardHistory.getWhen(),
                new CardWhereDto(cardHistory.getWhere().cardId(), cardHistory.getWhere().cardName()),
                cardHistory.getEventType(),
                cardHistory.getEventData(),
                cardHistory.getTarget()
        );
    }
}

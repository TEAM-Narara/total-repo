package com.narara.superboard.card.interfaces.dto.log;

import com.narara.superboard.card.document.CardHistory;
import com.narara.superboard.card.document.CardInfo;
import com.narara.superboard.common.constant.enums.EventData;
import com.narara.superboard.common.constant.enums.EventType;
import com.narara.superboard.common.document.Who;

public record CardLogDetailResponseDto<T>(
        Who who,
        Long when,
        CardWhereDto where,
        EventType eventType,
        EventData eventData,
        T target
) {
    public static <T extends CardInfo> CardLogDetailResponseDto<T> createLogDetailResponseDto(
            CardHistory<T> cardHistory
    ) {
        return new CardLogDetailResponseDto<>(
                cardHistory.getWho(),
                cardHistory.getWhen(),
                new CardWhereDto(cardHistory.getWhere().cardId(), cardHistory.getWhere().cardName()),
                cardHistory.getEventType(),
                cardHistory.getEventData(),
                cardHistory.getTarget()
        );
    }
}

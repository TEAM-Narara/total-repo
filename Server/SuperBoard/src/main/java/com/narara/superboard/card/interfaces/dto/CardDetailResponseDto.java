package com.narara.superboard.card.interfaces.dto;

import com.narara.superboard.card.entity.Card;
import lombok.Builder;

@Builder
public record CardDetailResponseDto(
        CardSimpleResponseDto cardSimpleResponseDto,
        String description,
        Long startAt,
        Long endAt,
        Long myOrder,
        Boolean isArchived,
        String coverType,
        String coverValue
) {
    public static CardDetailResponseDto of(Card card) {
        return of(card, null, null);
    }

    public static CardDetailResponseDto of(Card card, String coverType, String coverValue) {
        return CardDetailResponseDto.builder()
                .cardSimpleResponseDto(CardSimpleResponseDto.of(card))
                .description(card.getDescription())
                .startAt(card.getStartAt())
                .endAt(card.getEndAt())
                .myOrder(card.getMyOrder())
                .isArchived(card.getIsArchived())
                .coverType(coverType)
                .coverValue(coverValue)
                .build();
    }
}

package com.narara.superboard.card.interfaces.dto;

import com.narara.superboard.card.entity.Card;
import com.narara.superboard.common.interfaces.dto.CoverDto;
import lombok.Builder;

@Builder
public record CardDetailResponseDto(
        CardSimpleResponseDto cardSimpleResponseDto,
        String description,
        Long startAt,
        Long endAt,
        Long myOrder,
        Boolean isArchived,
        CoverDto cover
) {
    public static CardDetailResponseDto from(Card card) {
        return from(card, (String) card.getCover().get("type"), (String) card.getCover().get("value"));
    }

    public static CardDetailResponseDto from(Card card, String coverType, String coverValue) {
        return CardDetailResponseDto.builder()
                .cardSimpleResponseDto(CardSimpleResponseDto.of(card))
                .description(card.getDescription())
                .startAt(card.getStartAt())
                .endAt(card.getEndAt())
                .myOrder(card.getMyOrder())
                .isArchived(card.getIsArchived())
                .cover(new CoverDto(coverType, coverValue))
                .build();
    }
}

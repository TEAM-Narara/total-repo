package com.narara.superboard.card.interfaces.dto;

import com.narara.superboard.card.entity.Card;
import lombok.Builder;

@Builder
public record CardSimpleResponseDto(
        Long cardId,
        Long listId,
        String name
) {
    public static CardSimpleResponseDto of(Card card){
        return CardSimpleResponseDto.builder()
                .cardId(card.getId())
                .listId(card.getList().getId())
                .name(card.getName())
                .build();
    }
}

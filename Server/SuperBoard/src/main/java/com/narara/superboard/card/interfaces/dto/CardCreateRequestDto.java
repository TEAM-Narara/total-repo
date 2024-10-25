package com.narara.superboard.card.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.NameHolder;

public record CardCreateRequestDto(
        Long listId,
        String cardName
) implements NameHolder {
    @Override
    public String name() {
        return cardName;
    }
}

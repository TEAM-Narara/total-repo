package com.narara.superboard.list.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.NameHolder;
import lombok.Builder;

@Builder
public record ListUpdateRequestDto(
        Long listId,
        String listName
) implements NameHolder {
    @Override
    public String name() {
        return listName;
    }
}

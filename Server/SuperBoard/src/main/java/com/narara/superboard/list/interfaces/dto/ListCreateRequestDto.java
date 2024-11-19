package com.narara.superboard.list.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.NameHolder;

public record ListCreateRequestDto(
        Long boardId,
        String listName
) implements NameHolder {

    @Override
    public String name() {
        return listName;
    }
}

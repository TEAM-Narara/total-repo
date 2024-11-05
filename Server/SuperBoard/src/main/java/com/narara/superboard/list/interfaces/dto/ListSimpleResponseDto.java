package com.narara.superboard.list.interfaces.dto;

import com.narara.superboard.list.entity.List;

public record ListSimpleResponseDto(
        Long listId,
        Long boardId,
        String name,
        Long myOrder,
        Boolean isArchived
) {

    public static ListSimpleResponseDto of(List list) {
        return new ListSimpleResponseDto(
                list.getId(),
                list.getBoard().getId(),
                list.getName(),
                list.getMyOrder(),
                list.getIsArchived()
        );
    }
}

package com.narara.superboard.list.interfaces.dto;

import java.util.ArrayList;
import com.narara.superboard.list.entity.List;

public record ListMoveResponseDto(Long listId, Long myOrder) {
    public static java.util.List<ListMoveResponseDto> of(java.util.List<List> updatedListCollection) {
        java.util.List<ListMoveResponseDto> listMoveResponseDtos = new ArrayList<>();

        for (List list : updatedListCollection) {
            listMoveResponseDtos.add(new ListMoveResponseDto(list.getId(), list.getMyOrder()));
        }

        return listMoveResponseDtos;
    }
}

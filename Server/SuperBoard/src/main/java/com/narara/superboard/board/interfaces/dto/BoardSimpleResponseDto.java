package com.narara.superboard.board.interfaces.dto;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.application.handler.CoverHandler;
import lombok.Builder;

@Builder
public record BoardSimpleResponseDto(
        Long id,
        Long workspaceId,
        String name,
        String backgroundType,
        String backgroundValue,
        Boolean isClosed
) {
    public static BoardSimpleResponseDto of(Board board, CoverHandler coverHandler) {
       return BoardSimpleResponseDto.builder()
               .id(board.getId())
               .workspaceId(board.getWorkSpace().getId())
               .name(board.getName())
               .backgroundType(coverHandler.getTypeValue(board.getCover()))
               .backgroundValue(coverHandler.getValue(board.getCover()))
               .isClosed(board.getIsArchived())
               .build();
    }
}

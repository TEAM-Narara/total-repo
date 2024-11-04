package com.narara.superboard.board.interfaces.dto;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.common.application.handler.CoverHandler;
import lombok.Builder;

@Builder
public record BoardDetailResponseDto(
        Long id,
        Long workspaceId,
        String name,
        String backgroundType,
        String backgroundValue,
        String visibility,
        Boolean isClosed
) {
    public static BoardDetailResponseDto of(Board board, CoverHandler coverHandler) {
        return BoardDetailResponseDto.builder()
                .id(board.getId())
                .workspaceId(board.getWorkSpace().getId())
                .name(board.getName())
                .backgroundType(coverHandler.getTypeValue(board.getCover()))
                .backgroundValue(coverHandler.getValue(board.getCover()))
                .visibility(board.getVisibility().name())
                .isClosed(board.getIsArchived())
                .build();
    }

    public static BoardDetailResponseDto of(Board board) {
        return BoardDetailResponseDto.builder()
                .id(board.getId())
                .workspaceId(board.getWorkSpace().getId())
                .name(board.getName())
                .visibility(board.getVisibility().name())
                .isClosed(board.getIsArchived())
                .build();
    }
}

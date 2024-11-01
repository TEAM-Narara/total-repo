package com.narara.superboard.board.interfaces.dto;

public record BoardSimpleResponseDto(
        Long id,
        Long workspaceId,
        String name,
        String backgroundType,
        String backgroundValue,
        Boolean isClosed
) {
}

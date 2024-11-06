package com.narara.superboard.board.interfaces.dto;

import lombok.Builder;

@Builder
public record MyBoardResponseDto(
        Long id,
        Long workspaceId,
        String name,
        String backgroundType,
        String backgroundValue,
        String visibility,
        Boolean isClosed
) {
}

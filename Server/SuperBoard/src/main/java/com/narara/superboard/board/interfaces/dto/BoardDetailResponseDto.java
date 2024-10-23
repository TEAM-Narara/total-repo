package com.narara.superboard.board.interfaces.dto;

import lombok.Builder;

@Builder
public record BoardDetailResponseDto(
        Long id,
        String name,
        String backgroundType,
        String backgroundValue
) {
}

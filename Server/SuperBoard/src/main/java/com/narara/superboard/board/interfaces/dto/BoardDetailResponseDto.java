package com.narara.superboard.board.interfaces.dto;

public record BoardDetailResponseDto(
        Long boardId,
        String name,
        String backgroundType,
        String backgroundValue
) {
}

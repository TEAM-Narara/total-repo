package com.narara.superboard.board.interfaces.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record BoardCollectionResponseDto(
        List<BoardDetailResponseDto> boardDetailResponseDtoList
) {
}

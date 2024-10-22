package com.narara.superboard.board.interfaces.dto;

import java.util.List;

public record BoardCollectionResponseDto(
        List<BoardDetailResponseDto> boardDetailResponseDtoList
) {
}

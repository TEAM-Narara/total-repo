package com.narara.superboard.board.interfaces.dto.activity;

import java.util.List;

public record BoardActivityPageableResponseDto(
        List<BoardCombinedActivityDto> boardActivityList,
        Long totalElements,
        Long totalPages
) {
}

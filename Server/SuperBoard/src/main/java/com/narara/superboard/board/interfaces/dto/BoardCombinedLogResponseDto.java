package com.narara.superboard.board.interfaces.dto;

import com.narara.superboard.list.entity.List;

public record BoardCombinedLogResponseDto(List<BoardCombinedLogDto> activityList,
                                          Long totalPages,
                                          Long totalElements) {

}



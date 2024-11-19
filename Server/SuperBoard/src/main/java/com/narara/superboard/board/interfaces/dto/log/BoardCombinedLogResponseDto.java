package com.narara.superboard.board.interfaces.dto.log;


import java.util.List;

public record BoardCombinedLogResponseDto(List<BoardLogDetailResponseDto> logList,
                                          Long totalPages,
                                          Long totalElements) {

}



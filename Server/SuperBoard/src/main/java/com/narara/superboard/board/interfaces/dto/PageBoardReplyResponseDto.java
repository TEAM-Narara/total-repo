package com.narara.superboard.board.interfaces.dto;

import java.util.List;

public record PageBoardReplyResponseDto(List<BoardReplyCollectionResponseDto> boardReplyCollectionResponseDtos,
                                        Integer totalPages, Long totalElements) {
}

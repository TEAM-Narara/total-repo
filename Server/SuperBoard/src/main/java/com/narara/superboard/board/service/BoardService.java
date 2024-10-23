package com.narara.superboard.board.service;

import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;

public interface BoardService {
    BoardCollectionResponseDto getBoardCollectionResponseDto(Long workSpaceId);
}

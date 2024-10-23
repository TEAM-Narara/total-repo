package com.narara.superboard.board.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;
import com.narara.superboard.board.interfaces.dto.BoardCreateRequestDto;

public interface BoardService {
    BoardCollectionResponseDto getBoardCollectionResponseDto(Long workSpaceId);
    Long createBoard(BoardCreateRequestDto boardCreateRequestDto);
    Board getBoard(Long boardId);

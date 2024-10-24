package com.narara.superboard.board.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.interfaces.dto.BoardCollectionResponseDto;
import com.narara.superboard.board.interfaces.dto.BoardCreateRequestDto;
import com.narara.superboard.board.interfaces.dto.BoardUpdateByMemberRequestDto;
import com.narara.superboard.board.interfaces.dto.BoardUpdateRequestDto;

import java.util.List;

public interface BoardService {
    BoardCollectionResponseDto getBoardCollectionResponseDto(Long workSpaceId);

    Long createBoard(BoardCreateRequestDto boardCreateRequestDto);

    Board getBoard(Long boardId);

    void deleteBoard(Long boardId);

    Board updateBoard(Long boardId, BoardUpdateRequestDto boardUpdateRequestDto);

    Board updateBoardByMember(Long boardId, BoardUpdateByMemberRequestDto boardUpdateByMemberRequestDto);

    List<Board> getArchivedBoards(Long workspaceId);

    void updateArchiveStatus(Long boardId, boolean isArchived);

}
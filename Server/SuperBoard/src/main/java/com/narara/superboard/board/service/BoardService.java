package com.narara.superboard.board.service;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.interfaces.dto.*;

import com.narara.superboard.member.entity.Member;
import org.springframework.data.domain.Pageable;

import com.narara.superboard.websocket.constant.Action;
import com.narara.superboard.workspace.interfaces.dto.MyBoardCollectionResponse;

import java.util.List;

public interface BoardService {
    List<BoardDetailResponseDto> getBoardCollectionResponseDto(Long workSpaceId);

    Board createBoard(Long memberId, BoardCreateRequestDto boardCreateRequestDto);

    Board getBoard(Long boardId);

    void deleteBoard(Long boardId);

    Board updateBoard(Long memberId, Long boardId, BoardUpdateRequestDto boardUpdateRequestDto);

    Board updateBoardByMember(Long boardId, BoardUpdateByMemberRequestDto boardUpdateByMemberRequestDto);

    List<Board> getArchivedBoards(Long workspaceId);

    void changeArchiveStatus(Long boardId);

    PageBoardReplyResponseDto getRepliesByBoardId(Long boardId, Pageable pageable);



    void checkBoardMember(Board board, Member member, Action action);

    MyBoardCollectionResponse getMyBoardList(Long memberId);
}

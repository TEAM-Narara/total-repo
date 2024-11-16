package com.narara.superboard.board.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.interfaces.dto.activity.BoardActivityPageableResponseDto;
import com.narara.superboard.board.interfaces.dto.log.BoardCombinedLogResponseDto;
import com.narara.superboard.board.interfaces.dto.log.BoardLogDetailResponseDto;
import com.narara.superboard.board.interfaces.dto.*;

import com.narara.superboard.member.entity.Member;
import org.springframework.data.domain.Pageable;

import com.narara.superboard.websocket.constant.Action;
import com.narara.superboard.workspace.interfaces.dto.MyBoardCollectionResponse;

import java.util.List;

public interface BoardService {
    List<BoardDetailResponseDto> getBoardCollectionResponseDto(Long workspaceId);

    Board createBoard(Long memberId, BoardCreateRequestDto boardCreateRequestDto);

    Board getBoard(Long boardId);

    void deleteBoard(Member member, Long boardId);

    Board updateBoard(Long memberId, Long boardId, BoardUpdateRequestDto boardUpdateRequestDto);

    List<Board> getArchivedBoards(Long workspaceId);

    void changeArchiveStatus(Member member, Long boardId) throws FirebaseMessagingException;

    PageBoardReplyResponseDto getRepliesByBoardId(Long boardId, Pageable pageable);

    void checkBoardMember(Board board, Member member, Action action);

    MyBoardCollectionResponse getMyBoardList(Long memberId, String keyword);


    List<BoardLogDetailResponseDto> getAllLog(Long boardId);

    BoardCombinedLogResponseDto getBoardCombinedLog(Long boardId, Pageable pageable);

    BoardActivityPageableResponseDto getBoardActivity(Long boardId, Pageable pageable);

}

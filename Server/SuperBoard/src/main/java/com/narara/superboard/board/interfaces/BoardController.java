package com.narara.superboard.board.interfaces;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.interfaces.dto.*;
import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "보드", description = "보드 관련 API를 제공하는 인터페이스")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController implements BoardAPI {

    private final BoardService boardService;
    private final CoverHandler coverHandler;

    @Override
    @Operation(summary = "보드 컬렉션 조회", description = "워크스페이스 ID를 사용하여 워크스페이스 내의 모든 보드를 조회합니다.")
    public ResponseEntity<DefaultResponse<List<BoardDetailResponseDto>>> getBoardCollection(
            @PathVariable Long workspaceId) {
        List<BoardDetailResponseDto> boardCollection = boardService.getBoardCollectionResponseDto(workspaceId).boardDetailResponseDtoList();
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_FETCH_SUCCESS, boardCollection), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "보드 생성", description = "새로운 보드를 생성합니다.")
    public ResponseEntity<DefaultResponse<Long>> createBoard(
            @AuthenticationPrincipal Member member,
            @RequestBody BoardCreateRequestDto boardCreateRequestDto) {
        System.out.println("MEMBER : +" + member);
        Long boardId = boardService.createBoard(member, boardCreateRequestDto);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.BOARD_CREATE_SUCCESS, boardId), HttpStatus.CREATED);
    }

    @Override
    @Operation(summary = "보드 조회", description = "보드 ID를 사용하여 특정 보드를 조회합니다.")
    public ResponseEntity<DefaultResponse<BoardDetailResponseDto>> getBoard(@PathVariable Long boardId) {
        Board board = boardService.getBoard(boardId);
        BoardDetailResponseDto boardDetailResponseDto = BoardDetailResponseDto.of(board, coverHandler);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_FETCH_SUCCESS, boardDetailResponseDto), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "보드 삭제", description = "보드 ID를 사용하여 특정 보드를 삭제합니다.")
    public ResponseEntity<DefaultResponse<Void>> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_DELETE_SUCCESS), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "어드민 보드 수정", description = "보드 ID와 수정 정보를 사용하여 특정 보드를 수정합니다.")
    public ResponseEntity<DefaultResponse<BoardDetailResponseDto>> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardUpdateRequestDto boardUpdateRequestDto) {
        Board updatedBoard = boardService.updateBoard(boardId, boardUpdateRequestDto);
        BoardDetailResponseDto boardDetailResponseDto = BoardDetailResponseDto.of(updatedBoard, coverHandler);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_ADMIN_UPDATE_SUCCESS, boardDetailResponseDto), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "사용자 보드 설정 수정", description = "보드 ID와 사용자 수정 정보를 사용하여 사용자가 자신의 보드 설정을 업데이트합니다.")
    public ResponseEntity<DefaultResponse<BoardSimpleResponseDto>> updateBoardByMember(
            @PathVariable Long boardId,
            @RequestBody BoardUpdateByMemberRequestDto boardUpdateByMemberRequestDto) {
        Board updatedBoard = boardService.updateBoardByMember(boardId, boardUpdateByMemberRequestDto);
        BoardSimpleResponseDto boardSimpleResponseDto = BoardSimpleResponseDto.of(updatedBoard, coverHandler);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_MEMBER_UPDATE_SUCCESS, boardSimpleResponseDto), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "아카이브된 보드 조회", description = "워크스페이스 ID를 사용하여 아카이브된 보드 목록을 조회합니다.")
    public ResponseEntity<DefaultResponse<List<BoardSimpleResponseDto>>> getArchivedBoards(@PathVariable Long workspaceId) {
        List<Board> archivedBoards = boardService.getArchivedBoards(workspaceId);
        List<BoardSimpleResponseDto> boardSimpleResponseDtoList = new ArrayList<>();
        for (Board board : archivedBoards) {
            boardSimpleResponseDtoList.add(BoardSimpleResponseDto.of(board, coverHandler));
        }
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_ARCHIVED_FETCH_SUCCESS, boardSimpleResponseDtoList), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "보드 아카이브 상태 변경", description = "보드 ID를 사용하여 특정 보드의 아카이브 상태를 변경합니다.")
    public ResponseEntity<DefaultResponse<Void>> changeArchiveStatus(@PathVariable Long boardId) {
        boardService.changeArchiveStatus(boardId);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_ARCHIVE_STATUS_CHANGED), HttpStatus.OK);
    }
}

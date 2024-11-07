package com.narara.superboard.board.interfaces;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.interfaces.dto.*;
import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.interfaces.log.ActivityDetailResponseDto;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.common.service.IAuthenticationFacade;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.interfaces.dto.MyBoardCollectionResponse;
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
    private final IAuthenticationFacade authenticationFacade;
    private final BoardService boardService;
    private final CoverHandler coverHandler;

    @Override
    @Operation(summary = "보드 컬렉션 조회", description = "워크스페이스 ID를 사용하여 워크스페이스 내의 모든 보드를 조회합니다.")
    public ResponseEntity<DefaultResponse<List<BoardDetailResponseDto>>> getBoardCollection(
            @PathVariable Long workspaceId) {
        List<BoardDetailResponseDto> boardCollection = boardService.getBoardCollectionResponseDto(workspaceId);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_FETCH_SUCCESS, boardCollection), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "보드 생성", description = "새로운 보드를 생성합니다.")
    public ResponseEntity<DefaultResponse<Long>> createBoard(@RequestBody BoardCreateRequestDto boardCreateRequestDto) {
        Long memberId = getMemberId();
        Board board = boardService.createBoard(memberId, boardCreateRequestDto);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.BOARD_CREATE_SUCCESS, board.getId()), HttpStatus.CREATED);
    }

    private Long getMemberId() {
        return authenticationFacade.getAuthenticatedUser().getUserId();
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
    public ResponseEntity<DefaultResponse<Void>> deleteBoard(@AuthenticationPrincipal Member member, @PathVariable Long boardId) {
        boardService.deleteBoard(member, boardId);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_DELETE_SUCCESS), HttpStatus.OK);
    }

    @Override
    @Operation(
            summary = "보드 수정",
            description = "보드 ID와 수정 정보를 사용하여 특정 보드를 수정합니다. visibility의 경우 ADMIN만 수정이 가능하며, MEMBER가 수정하려는 경우 에러가 발생"
    )
    public ResponseEntity<DefaultResponse<BoardDetailResponseDto>> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardUpdateRequestDto boardUpdateRequestDto) {
        Long memberId = getMemberId();
        Board updatedBoard = boardService.updateBoard(memberId, boardId, boardUpdateRequestDto);
        BoardDetailResponseDto boardDetailResponseDto = BoardDetailResponseDto.of(updatedBoard, coverHandler);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_ADMIN_UPDATE_SUCCESS, boardDetailResponseDto), HttpStatus.OK);
    }

//    @Override //프론트 팀원의 요구사항을 반영, 어드민 보드 수정 하나로 퉁침. 백엔드 팀원 모두 확인 후, 나중에 삭제 TODO
//    @Operation(summary = "사용자 보드 설정 수정", description = "보드 ID와 사용자 수정 정보를 사용하여 사용자가 자신의 보드 설정을 업데이트합니다.")
//    public ResponseEntity<DefaultResponse<BoardSimpleResponseDto>> updateBoardByMember(
//            @PathVariable Long boardId,
//            @RequestBody BoardUpdateByMemberRequestDto boardUpdateByMemberRequestDto) {
//        Board updatedBoard = boardService.updateBoardByMember(boardId, boardUpdateByMemberRequestDto);
//        BoardSimpleResponseDto boardSimpleResponseDto = BoardSimpleResponseDto.of(updatedBoard, coverHandler);
//        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_MEMBER_UPDATE_SUCCESS, boardSimpleResponseDto), HttpStatus.OK);
//    }

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
    public ResponseEntity<DefaultResponse<Void>> changeArchiveStatus(@AuthenticationPrincipal Member member, @PathVariable Long boardId) {
        boardService.changeArchiveStatus(member, boardId);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_ARCHIVE_STATUS_CHANGED), HttpStatus.OK);
    }

    @Override
    @Operation(summary = "보드의 액티비티 목록 조회", description = "보드의 액티비티 목록 조회")
    public ResponseEntity<DefaultResponse<List<ActivityDetailResponseDto>>> getBoardActivity(Long boardId) {
        List<ActivityDetailResponseDto> boardActivity = boardService.getBoardActivity(boardId);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_ACTIVITY_FETCH_SUCCESS, boardActivity), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "내가 권한이 있는 보드들 목록 조회", description = "권한이 있는 보드들을 모두 불러옵니다. keyword가 null이면 전체조회")
    public ResponseEntity<DefaultResponse<MyBoardCollectionResponse>> getMyBoardList(@RequestParam(value = "keyword", required = false)String keyword) {
        Long memberId = getMemberId();
        MyBoardCollectionResponse myBoardList = boardService.getMyBoardList(memberId, keyword);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_FETCH_SUCCESS, myBoardList), HttpStatus.OK);
    }
}

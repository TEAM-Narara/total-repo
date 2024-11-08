package com.narara.superboard.board.interfaces;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.board.interfaces.dto.*;
import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.common.application.handler.CoverHandler;
import com.narara.superboard.common.interfaces.log.BoardActivityDetailResponseDto;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.common.service.IAuthenticationFacade;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.interfaces.dto.MyBoardCollectionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "5. 보드")
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
    public ResponseEntity<DefaultResponse<BoardDetailResponseDto>> createBoard(@RequestBody BoardCreateRequestDto boardCreateRequestDto) {
        Long memberId = getMemberId();
        Board board = boardService.createBoard(memberId, boardCreateRequestDto);
        BoardDetailResponseDto boardDetailResponseDto = BoardDetailResponseDto.of(board);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.CREATED, ResponseMessage.BOARD_CREATE_SUCCESS, boardDetailResponseDto), HttpStatus.CREATED);
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
    public ResponseEntity<DefaultResponse<List<BoardActivityDetailResponseDto>>> getBoardActivity(Long boardId) {
        List<BoardActivityDetailResponseDto> boardActivity = boardService.getBoardActivity(boardId);
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_LOG_FETCH_SUCCESS, boardActivity), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "내가 권한이 있는 보드들 목록 조회", description = "권한이 있는 보드들을 모두 불러옵니다. keyword가 null이면 전체조회")
    public ResponseEntity<DefaultResponse<MyBoardCollectionResponse>> getMyBoardList(@RequestParam(value = "keyword", required = false) String keyword) {
        Long memberId = getMemberId();
        MyBoardCollectionResponse myBoardList = boardService.getMyBoardList(memberId, keyword);

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_FETCH_SUCCESS, myBoardList), HttpStatus.OK);
    }


    @Parameters({
            @Parameter(name = "page", description = "조회할 페이지 번호 (1부터 시작)", example = "1", schema = @Schema(defaultValue = "1")),
            @Parameter(name = "size", description = "페이지당 항목 수", example = "10", schema = @Schema(defaultValue = "10"))
    })
    @Override
    public ResponseEntity<DefaultResponse<List<BoardCombinedLogResponseDto>>> getBoardCombinedLog(
            @PathVariable Long boardId,
            @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        List<BoardCombinedLogResponseDto> combinedLogs = boardService.getBoardCombinedLog(boardId, pageable);

        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.BOARD_ACTIVITY_FETCH_SUCCESS, combinedLogs)
        );
    }
}

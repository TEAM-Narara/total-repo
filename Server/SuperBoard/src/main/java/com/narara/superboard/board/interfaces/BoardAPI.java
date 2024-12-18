package com.narara.superboard.board.interfaces;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.narara.superboard.board.interfaces.dto.*;
import com.narara.superboard.board.interfaces.dto.activity.BoardActivityPageableResponseDto;
import com.narara.superboard.board.interfaces.dto.log.BoardLogDetailResponseDto;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/boards")
public interface BoardAPI {

    @GetMapping("/workspace/{workspaceId}")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'MEMBER')")
    @Operation(summary = "워크스페이스의 모든 보드 조회")
    ResponseEntity<DefaultResponse<List<BoardDetailResponseDto>>> getBoardCollection(@PathVariable Long workspaceId);

    @PostMapping("")
    @PreAuthorize("hasPermission(#boardCreateRequestDto.workspaceId(), 'WORKSPACE', 'MEMBER')")
    @Operation(summary = "보드 생성")
    ResponseEntity<DefaultResponse<BoardDetailResponseDto>> createBoard(
            @RequestBody BoardCreateRequestDto boardCreateRequestDto);

    @GetMapping("/{boardId}")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'MEMBER')")
    @Operation(summary = "특정 보드 조회")
    ResponseEntity<DefaultResponse<BoardDetailResponseDto>> getBoard(@PathVariable Long boardId);

    @DeleteMapping("/{boardId}")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'ADMIN')")
    @Operation(summary = "보드 삭제")
    ResponseEntity<DefaultResponse<Void>> deleteBoard(@AuthenticationPrincipal Member member, @PathVariable Long boardId);

    @PatchMapping("/{boardId}")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'MEMBER')")
    @Operation(summary = "보드 수정")
    ResponseEntity<DefaultResponse<BoardDetailResponseDto>> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardUpdateRequestDto boardUpdateRequestDto);

//    @PatchMapping("/{boardId}/member")
//    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'MEMBER')")
//    @Operation(summary = "사용자가 자신의 보드 설정을 업데이트")
//    ResponseEntity<DefaultResponse<BoardSimpleResponseDto>> updateBoardByMember(
//            @PathVariable Long boardId,
//            @RequestBody BoardUpdateByMemberRequestDto boardUpdateByMemberRequestDto);

    @GetMapping("/workspace/{workspaceId}/archived")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'MEMBER')")
    @Operation(summary = "아카이브된 보드 조회")
    ResponseEntity<DefaultResponse<List<BoardSimpleResponseDto>>> getArchivedBoards(@PathVariable Long workspaceId);

    @PatchMapping("/{boardId}/archive")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'MEMBER')")
    @Operation(summary = "보드 아카이브 상태 변경")
    ResponseEntity<DefaultResponse<Void>> changeArchiveStatus(@AuthenticationPrincipal Member member, @PathVariable Long boardId)
            throws FirebaseMessagingException;

    @GetMapping("{boardId}/log")
    @Operation(summary = "보드의 모든 로그 목록 조회", description = "보드의 모든 로그 목록 조회")
    ResponseEntity<DefaultResponse<List<BoardLogDetailResponseDto>>> getBoardLog(Long boardId);

    @GetMapping("/{boardId}/activity")
    ResponseEntity<DefaultResponse<BoardActivityPageableResponseDto>> getBoardActivity(
            @PathVariable Long boardId,
            @RequestParam int page,
            @RequestParam int size
    );
}

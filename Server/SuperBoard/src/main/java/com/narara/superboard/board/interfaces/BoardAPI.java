package com.narara.superboard.board.interfaces;

import com.narara.superboard.board.interfaces.dto.*;
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

    @PostMapping("/")
    @PreAuthorize("hasPermission(#boardCreateRequestDto.workSpaceId(), 'WORKSPACE', 'MEMBER')")
    @Operation(summary = "보드 생성")
    ResponseEntity<DefaultResponse<Long>> createBoard(
            @AuthenticationPrincipal Member member,
            @RequestBody BoardCreateRequestDto boardCreateRequestDto);

    @GetMapping("/{boardId}")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'MEMBER')")
    @Operation(summary = "특정 보드 조회")
    ResponseEntity<DefaultResponse<BoardDetailResponseDto>> getBoard(@PathVariable Long boardId);

    @DeleteMapping("/{boardId}")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'ADMIN')")
    @Operation(summary = "보드 삭제")
    ResponseEntity<DefaultResponse<Void>> deleteBoard(@PathVariable Long boardId);

    @PatchMapping("/{boardId}")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'ADMIN')")
    @Operation(summary = "보드 수정")
    ResponseEntity<DefaultResponse<BoardDetailResponseDto>> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardUpdateRequestDto boardUpdateRequestDto);

    @PatchMapping("/{boardId}/member")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'MEMBER')")
    @Operation(summary = "사용자가 자신의 보드 설정을 업데이트")
    ResponseEntity<DefaultResponse<BoardSimpleResponseDto>> updateBoardByMember(
            @PathVariable Long boardId,
            @RequestBody BoardUpdateByMemberRequestDto boardUpdateByMemberRequestDto);

    @GetMapping("/workspace/{workspaceId}/archived")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'MEMBER')")
    @Operation(summary = "아카이브된 보드 조회")
    ResponseEntity<DefaultResponse<List<BoardSimpleResponseDto>>> getArchivedBoards(@PathVariable Long workspaceId);

    @PatchMapping("/{boardId}/archive")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'MEMBER')")
    @Operation(summary = "보드 아카이브 상태 변경")
    ResponseEntity<DefaultResponse<Void>> changeArchiveStatus(@PathVariable Long boardId);
}

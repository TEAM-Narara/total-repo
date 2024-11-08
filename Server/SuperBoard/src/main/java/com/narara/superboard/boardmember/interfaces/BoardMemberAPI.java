package com.narara.superboard.boardmember.interfaces;

import com.narara.superboard.boardmember.interfaces.dto.AddMemberDto;
import com.narara.superboard.boardmember.interfaces.dto.BoardMemberResponseDto;
import com.narara.superboard.boardmember.interfaces.dto.EditBoardMemberAuthorityDto;
import com.narara.superboard.boardmember.interfaces.dto.MemberResponseDto;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/v1/boards/{boardId}")
public interface BoardMemberAPI {

    @Operation(summary = "보드 멤버 목록 조회", description = "지정된 보드에 속한 모든 멤버 정보를 조회합니다.")
    @GetMapping("/members")
    ResponseEntity<DefaultResponse<BoardMemberResponseDto>> getBoardMembers(
            @Parameter(description = "조회할 보드의 ID", required = true) @PathVariable Long boardId
    );

    @Operation(summary = "사용자의 보드 알림 상태 조회", description = "사용자의 워치(알림) 상태를 조회합니다.")
    @GetMapping("/member/watch-status")
    ResponseEntity<DefaultResponse<Boolean>> getWatchStatus(
            @Parameter(description = "조회할 보드의 ID", required = true) @PathVariable Long boardId,
            @AuthenticationPrincipal @Parameter(hidden = true) Member member
    );

    @Operation(summary = "사용자의 보드 알림 상태 변경", description = "사용자의 워치(알림) 상태를 토글합니다.")
    @PutMapping("/member/watch-status")
    ResponseEntity<DefaultResponse<Void>> updateWatchStatus(
            @Parameter(description = "변경할 보드의 ID", required = true) @PathVariable Long boardId,
            @AuthenticationPrincipal @Parameter(hidden = true) Member member
    );

    @Operation(summary = "보드 멤버 추가", description = "이미 보드의 멤버로 추가되어 있는 경우에는 요청을 무시하고 있는 값을 보내줌")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'ADMIN')") //boardMember 추가는 ADMIN만 가능
    @PostMapping("/member")
    ResponseEntity<DefaultResponse<MemberResponseDto>> addBoardMember(@PathVariable("boardId") Long boardId, @RequestBody AddMemberDto dto);

    @Operation(summary = "보드 멤버 삭제", description = "삭제한 친구를 보내줌")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'ADMIN')") //boardMember 추가는 ADMIN만 가능
    @DeleteMapping("/member")
    ResponseEntity<DefaultResponse<MemberResponseDto>> deleteBoardMember(@PathVariable("boardId") Long boardId, @RequestBody AddMemberDto dto);

    @Operation(summary = "보드 멤버 권한 수정", description = "수정한 결과값을 보내줌. ADMIN만 다른 멤버의 권한 수정이 가능함")
    @PreAuthorize("hasPermission(#boardId, 'BOARD', 'ADMIN')") //boardMember 추가는 ADMIN만 가능
    @PatchMapping("/member")
    ResponseEntity<DefaultResponse<MemberResponseDto>> editBoardMemberAuthority(@PathVariable("boardId") Long boardId, @RequestBody EditBoardMemberAuthorityDto dto);
}

package com.narara.superboard.boardmember.interfaces;

import com.narara.superboard.boardmember.interfaces.dto.BoardMemberCollectionResponseDto;
import com.narara.superboard.boardmember.interfaces.dto.BoardMemberResponseDto;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "보드 멤버 API", description = "보드 멤버 관련 기능을 제공하는 API")
@RequestMapping("/api/v1/boards/{boardId}")
public interface BoardMemberAPI {

    @Operation(summary = "보드 멤버 목록 조회", description = "지정된 보드에 속한 모든 멤버 정보를 조회합니다.")
    @GetMapping("/members")
    ResponseEntity<DefaultResponse<List<BoardMemberResponseDto>>> getBoardMembers(
            @Parameter(description = "조회할 보드의 ID", required = true) @PathVariable Long boardId
    );

    @Operation(summary = "워치 상태 조회", description = "사용자의 워치(알림) 상태를 조회합니다.")
    @GetMapping("/member/watch-status")
    ResponseEntity<DefaultResponse<Boolean>> getWatchStatus(
            @Parameter(description = "조회할 보드의 ID", required = true) @PathVariable Long boardId,
            @AuthenticationPrincipal @Parameter(hidden = true) Member member
    );

    @Operation(summary = "워치 상태 변경", description = "사용자의 워치(알림) 상태를 토글합니다.")
    @PutMapping("/member/watch-status")
    ResponseEntity<DefaultResponse<Void>> updateWatchStatus(
            @Parameter(description = "변경할 보드의 ID", required = true) @PathVariable Long boardId,
            @AuthenticationPrincipal @Parameter(hidden = true) Member member
    );
}

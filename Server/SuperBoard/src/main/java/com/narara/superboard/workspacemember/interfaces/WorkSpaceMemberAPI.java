package com.narara.superboard.workspacemember.interfaces;

import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1")
@Tag(name = "워크스페이스 회원", description = "워크스페이스 멤버 관련 API")
public interface WorkSpaceMemberAPI {

    @Operation(summary = "워크스페이스 멤버 조회", description = "워크스페이스 ID를 통해 멤버 리스트를 조회합니다.")
    @GetMapping("/workspaces/{workspaceId}/members")
    ResponseEntity<DefaultResponse<MemberCollectionResponseDto>> getWorkspaceMemberCollectionResponseDto(
            @Parameter(description = "조회할 워크스페이스의 ID", required = true) @PathVariable Long workspaceId
    );

    @Operation(summary = "멤버의 워크스페이스 리스트 조회", description = "멤버 ID를 통해 멤버가 속한 워크스페이스 리스트를 조회합니다.")
    @GetMapping("/member/workspaces")
    ResponseEntity<DefaultResponse<WorkSpaceListResponseDto>> getMemberWorkspaceList(
            @AuthenticationPrincipal @Parameter(hidden = true) Member member
    );
}

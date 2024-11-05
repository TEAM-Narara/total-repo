package com.narara.superboard.workspacemember.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceResponseDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkSpaceMemberDetailResponseDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberCollectionResponseDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "WorkspaceMember", description = "워크스페이스 멤버 관련 API")
public interface WorkSpaceMemberAPI {

    @Operation(summary = "워크스페이스 멤버 조회", description = "워크스페이스 ID를 통해 멤버 리스트를 조회합니다.")
    @GetMapping("/api/workspace/{workspaceId}/members")
    ResponseEntity<DefaultResponse<List<WorkSpaceMemberDetailResponseDto>>> getWorkspaceMemberCollectionResponseDto(
            @Parameter(description = "조회할 워크스페이스의 ID", required = true) @PathVariable Long workspaceId
    );


    @Hidden
    @Operation(summary = "멤버의 워크스페이스 리스트 조회", description = "멤버 ID를 통해 멤버가 속한 워크스페이스 리스트를 조회합니다.")
    @GetMapping("/api/member/workspaces")
    ResponseEntity<DefaultResponse<List<WorkSpaceResponseDto>>> getMemberWorkspaceList(
            @AuthenticationPrincipal @Parameter(hidden = true) Member member
    );
}
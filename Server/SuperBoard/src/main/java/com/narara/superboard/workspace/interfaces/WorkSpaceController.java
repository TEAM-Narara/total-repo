package com.narara.superboard.workspace.interfaces;

import com.narara.superboard.common.service.IAuthenticationFacade;
import com.narara.superboard.member.service.MemberService;
import com.narara.superboard.member.util.JwtTokenProvider;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;

import com.narara.superboard.workspace.interfaces.dto.WorkSpaceUpdateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceDiffDto;
import com.narara.superboard.workspace.service.WorkSpaceService;
import com.narara.superboard.workspace.service.mongo.WorkspaceOffsetService;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceCreateData;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WorkSpaceController implements WorkSpaceAPI {
    private final WorkSpaceService workSpaceService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final IAuthenticationFacade authenticationFacade;
    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkspaceOffsetService workspaceOffsetService;

    @Operation(summary = "워크스페이스 생성")
    @PostMapping
    public ResponseEntity<WorkspaceCreateData> createWorkSpace(WorkSpaceCreateRequestDto workspaceCreateRequestDto) {
        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();

        WorkSpace workSpace = workSpaceService.createWorkSpace(
                memberId,
                workspaceCreateRequestDto
        );

        return ResponseEntity.ok(new WorkspaceCreateData(workSpace.getId(), workSpace.getName()));
    }

    @Operation(summary = "워크스페이스 삭제")
    @DeleteMapping("/{workspaceId}")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')") //ADMIN만 가능
    public ResponseEntity deleteWorkspace(@PathVariable Long workspaceId) {
//        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();
        workSpaceService.deleteWorkSpace(workspaceId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "워크스페이스 수정")
    @PatchMapping("/{workspaceId}")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'MEMBER')") //MEMBER와 ADMIN만 가능
    public ResponseEntity<WorkspaceCreateData> editWorkspace(@PathVariable Long workspaceId, @RequestBody WorkSpaceUpdateRequestDto requestDto) {
        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();
        WorkSpace workSpace = workSpaceService.updateWorkSpace(workspaceId, requestDto.name());

        return ResponseEntity.ok(new WorkspaceCreateData(workSpace.getId(), workSpace.getName()));
    }

    @Operation(summary = "나의 워크스페이스 리스트 조회")
    @GetMapping
    public ResponseEntity<WorkSpaceListResponseDto> getWorkspaceListByMember() {
        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();
        List<WorkSpaceMember> workSpaceMemberList = workSpaceMemberRepository.findAllByMemberId(memberId);
        WorkSpaceListResponseDto workSpaceListResponseDto = WorkSpaceListResponseDto.from(workSpaceMemberList);

        return ResponseEntity.ok(workSpaceListResponseDto);
    }

    @Operation(summary = "특정 offset 이후 데이터 싹 조회")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'MEMBER')") //MEMBER와 ADMIN만 가능
    @GetMapping("/{workspaceId}/diffs")
    public ResponseEntity<List<WorkspaceDiffDto>> getDiffs(
            @PathVariable Long workspaceId,
            @RequestParam(required = false, defaultValue = "0") Long fromOffset
    ) {
        List<WorkspaceDiffDto> diffs = workspaceOffsetService.getDiffListFromOffset(workspaceId, fromOffset);
        return ResponseEntity.ok(diffs);
    }
}

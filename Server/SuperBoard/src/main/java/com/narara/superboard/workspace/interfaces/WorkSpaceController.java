package com.narara.superboard.workspace.interfaces;

import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.common.service.IAuthenticationFacade;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.service.MemberService;
import com.narara.superboard.member.util.JwtTokenProvider;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;

import com.narara.superboard.workspace.interfaces.dto.WorkSpaceResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceUpdateRequestDto;
import com.narara.superboard.workspace.service.WorkSpaceService;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import com.narara.superboard.workspacemember.service.WorkSpaceMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceCreateData;

import java.util.List;

@Tag(name = "4. 워크스페이스")
@RestController
@RequiredArgsConstructor
public class WorkSpaceController implements WorkSpaceAPI {
    private final WorkSpaceService workSpaceService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final IAuthenticationFacade authenticationFacade;
    private final WorkSpaceMemberService workspaceMemberService;
//    private final WorkspaceOffsetService workspaceOffsetService;

    @Operation(summary = "워크스페이스 생성")
    @PostMapping
    public ResponseEntity<DefaultResponse<WorkSpaceResponseDto>> createWorkSpace(WorkSpaceCreateRequestDto workspaceCreateRequestDto) {
        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();

        WorkSpaceMember workSpaceMember = workSpaceService.createWorkSpace(
                memberId,
                workspaceCreateRequestDto
        );

        WorkSpaceResponseDto workSpaceResponseDto = WorkSpaceResponseDto.from(workSpaceMember);

        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.WORKSPACE_CREATE_SUCCESS, workSpaceResponseDto));
    }

    @Operation(summary = "워크스페이스 삭제")
    @DeleteMapping("/{workspaceId}")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')") //ADMIN만 가능
    public ResponseEntity deleteWorkspace(@PathVariable Long workspaceId) {
        workSpaceService.deleteWorkSpace(workspaceId);

        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.WORKSPACE_DELETE_SUCCESS));
    }

    @Operation(summary = "워크스페이스 수정")
    @PatchMapping("/{workspaceId}")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'MEMBER')") //MEMBER와 ADMIN만 가능
    public ResponseEntity<DefaultResponse<WorkspaceCreateData>> editWorkspace(@PathVariable Long workspaceId, @RequestBody WorkSpaceUpdateRequestDto requestDto) {
        WorkSpace workSpace = workSpaceService.updateWorkSpace(workspaceId, requestDto.name());
        WorkspaceCreateData workspaceCreateData = new WorkspaceCreateData(workSpace.getId(), workSpace.getName());

        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.WORKSPACE_UPDATE_SUCCESS, workspaceCreateData));
    }

    @Operation(summary = "나의 워크스페이스 리스트 조회", hidden = true)
    @GetMapping
    public ResponseEntity<DefaultResponse<List<WorkSpaceResponseDto>>> getWorkspaceListByMember(@AuthenticationPrincipal Member member) {
        List<WorkSpaceResponseDto> workSpaceResponseDtos = workspaceMemberService.getMemberWorkspaceList(member);

        return ResponseEntity.ok(DefaultResponse.res(StatusCode.OK, ResponseMessage.WORKSPACE_LIST_FETCH_SUCCESS, workSpaceResponseDtos));
    }

//    @Operation(summary = "특정 offset 이후 데이터 싹 조회")
//    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'MEMBER')") //MEMBER와 ADMIN만 가능
//    @GetMapping("/{workspaceId}/diffs")
//    public ResponseEntity<List<WorkspaceDiffDto>> getDiffs(
//            @PathVariable Long workspaceId,
//            @RequestParam(required = false, defaultValue = "0") Long fromOffset
//    ) {
//        List<WorkspaceDiffDto> diffs = workspaceOffsetService.getDiffListFromOffset(workspaceId, fromOffset);
//        return ResponseEntity.ok(diffs);
//    }
}

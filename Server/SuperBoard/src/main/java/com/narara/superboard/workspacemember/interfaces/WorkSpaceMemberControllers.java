package com.narara.superboard.workspacemember.interfaces;

import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberDeleteRequest;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberRequest;
import com.narara.superboard.workspacemember.service.WorkSpaceMemberService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkSpaceMemberControllers implements WorkSpaceMemberAPI {
    private final WorkSpaceMemberService workSpaceMemberService;

    @Override
    public ResponseEntity<DefaultResponse<MemberCollectionResponseDto>> getWorkspaceMemberCollectionResponseDto(
            Long workspaceId) {
        MemberCollectionResponseDto responseDto = workSpaceMemberService.getWorkspaceMemberCollectionResponseDto(
                workspaceId);

        return new ResponseEntity<>(DefaultResponse.res(
                StatusCode.OK, ResponseMessage.WORKSPACE_MEMBER_FETCH_SUCCESS, responseDto)
                , HttpStatus.OK);

    }

    @Override
    public ResponseEntity<DefaultResponse<WorkSpaceListResponseDto>> getMemberWorkspaceList(
            @AuthenticationPrincipal Member member) {
        WorkSpaceListResponseDto responseDto = workSpaceMemberService.getMemberWorkspaceList(member);

        return new ResponseEntity<>(DefaultResponse.res(
                StatusCode.OK, ResponseMessage.MEMBER_WORKSPACE_LIST_FETCH_SUCCESS, responseDto)
                , HttpStatus.OK);
    }

    @Operation(summary = "워크스페이스 멤버 권한 수정")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')") //WORKSPACE의 ADMIN만 멤버권한 수정가능
    @PatchMapping("/{workspaceId}/members")
    public ResponseEntity<DefaultResponse<WorkspaceMemberDto>> editWorkspaceMemberAuthority(@PathVariable Long workspaceId, @RequestBody WorkspaceMemberDto requestDto) {
        WorkSpaceMember workSpaceMember = workSpaceMemberService.editAuthority(requestDto.memberId(), workspaceId, requestDto.authority());

        return ResponseEntity.ok(
                DefaultResponse.res(
                        StatusCode.OK,
                        ResponseMessage.WORKSPACE_UPDATE_SUCCESS,
                        new WorkspaceMemberDto(
                            workSpaceMember.getMember().getId(),
                            workSpaceMember.getAuthority()
                    )
                )
        );
    }

    @Operation(summary = "워크스페이스 멤버 추가")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')") //WORKSPACE의 ADMIN만 추가가능
    @PostMapping("/{workspaceId}/members")
    public ResponseEntity<DefaultResponse<WorkspaceMemberDto>> addWorkspaceMember(@PathVariable Long workspaceId, @RequestBody WorkspaceMemberRequest requestDto) {
        WorkSpaceMember workSpaceMember = workSpaceMemberService.addMember(workspaceId, requestDto.memberId(),
                requestDto.authority());

        return ResponseEntity.ok(
                DefaultResponse.res(
                        StatusCode.OK,
                        ResponseMessage.WORKSPACE_MEMBER_FETCH_SUCCESS,
                        new WorkspaceMemberDto(
                            workSpaceMember.getMember().getId(),
                          workSpaceMember.getAuthority()
                        )
                )
        );
    }

    @Operation(summary = "워크스페이스 멤버 삭제")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')") //WORKSPACE의 ADMIN만 삭제가능
    @DeleteMapping("/{workspaceId}/members")
    public ResponseEntity<DefaultResponse<WorkspaceMemberDto>> deleteWorkspaceMember(@PathVariable Long workspaceId, @RequestBody WorkspaceMemberDeleteRequest requestDto) {
        WorkSpaceMember workSpaceMember = workSpaceMemberService.deleteMember(workspaceId, requestDto.memberId());

        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.WORKSPACE_MEMBER_FETCH_SUCCESS, new WorkspaceMemberDto(
                        workSpaceMember.getMember().getId(),
                        workSpaceMember.getAuthority()
                ))
        );
    }

    @Operation(summary = "워크스페이스 멤버 조회")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'MEMBER')") // MEMBER만 조회가능
    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<DefaultResponse<MemberCollectionResponseDto>> getWorkspaceMember(@PathVariable Long workspaceId) {
        List<WorkSpaceMember> workSpaceMember = workSpaceMemberService.getWorkspaceMember(workspaceId);

        return ResponseEntity.ok(
                DefaultResponse.res(StatusCode.OK, ResponseMessage.WORKSPACE_MEMBER_FETCH_SUCCESS, MemberCollectionResponseDto.from(workSpaceMember))
        );
    }
}

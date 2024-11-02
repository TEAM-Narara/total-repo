package com.narara.superboard.workspacemember.interfaces;

import com.narara.superboard.common.service.IAuthenticationFacade;
import com.narara.superboard.member.service.MemberService;
import com.narara.superboard.member.util.JwtTokenProvider;
import com.narara.superboard.workspace.service.WorkSpaceService;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberDeleteRequest;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberListDto;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberRequest;
import com.narara.superboard.workspacemember.service.WorkSpaceMemberService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/workspaces")
@Slf4j
@RestController
@RequiredArgsConstructor
public class WorkspaceMemberController {
    private final WorkSpaceService workSpaceService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final IAuthenticationFacade authenticationFacade;
    private final WorkSpaceMemberService workSpaceMemberService;

    @Operation(summary = "워크스페이스 멤버 권한 수정")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')") //WORKSPACE의 ADMIN만 멤버권한 수정가능
    @PatchMapping("/{workspaceId}/members")
    public ResponseEntity<WorkspaceMemberDto> editWorkspaceMemberAuthority(@PathVariable Long workspaceId, @RequestBody WorkspaceMemberDto requestDto) {
        WorkSpaceMember workSpaceMember = workSpaceMemberService.editAuthority(requestDto.memberId(), workspaceId, requestDto.authority());

        return ResponseEntity.ok(
                new WorkspaceMemberDto(
                        workSpaceMember.getMember().getId(),
                        workSpaceMember.getAuthority()
                )
        );
    }

    @Operation(summary = "워크스페이스 멤버 추가")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')") //WORKSPACE의 ADMIN만 추가가능
    @PostMapping("/{workspaceId}/members")
    public ResponseEntity<WorkspaceMemberDto> addWorkspaceMember(@PathVariable Long workspaceId, @RequestBody WorkspaceMemberRequest requestDto) {
        WorkSpaceMember workSpaceMember = workSpaceMemberService.addMember(workspaceId, requestDto.memberId(),
                requestDto.authority());

        return ResponseEntity.ok(
                new WorkspaceMemberDto(
                        workSpaceMember.getMember().getId(),
                        workSpaceMember.getAuthority()
                )
        );
    }

    @Operation(summary = "워크스페이스 멤버 삭제")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')") //WORKSPACE의 ADMIN만 삭제가능
    @DeleteMapping("/{workspaceId}/members")
    public ResponseEntity<WorkspaceMemberDto> deleteWorkspaceMember(@PathVariable Long workspaceId, @RequestBody WorkspaceMemberDeleteRequest requestDto) {
        WorkSpaceMember workSpaceMember = workSpaceMemberService.deleteMember(workspaceId, requestDto.memberId());

        return ResponseEntity.ok(
                new WorkspaceMemberDto(
                        workSpaceMember.getMember().getId(),
                        workSpaceMember.getAuthority()
                )
        );
    }

    @Operation(summary = "워크스페이스 멤버 조회")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'MEMBER')") // MEMBER만 조회가능
    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<WorkspaceMemberListDto> getWorkspaceMember(@PathVariable Long workspaceId) {
        List<WorkSpaceMember> workSpaceMember = workSpaceMemberService.getWorkspaceMember(workspaceId);

        return ResponseEntity.ok(
                WorkspaceMemberListDto.from(workSpaceMember)
        );
    }
}
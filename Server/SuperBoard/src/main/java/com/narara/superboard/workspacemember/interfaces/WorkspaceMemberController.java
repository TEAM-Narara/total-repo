package com.narara.superboard.workspacemember.interfaces;

import com.narara.superboard.common.service.IAuthenticationFacade;
import com.narara.superboard.member.service.MemberService;
import com.narara.superboard.member.util.JwtTokenProvider;
import com.narara.superboard.workspace.service.WorkSpaceService;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.interfaces.dto.WorkspaceMemberEditDto;
import com.narara.superboard.workspacemember.service.WorkSpaceMemberService;
import io.swagger.v3.oas.annotations.Operation;
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
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')") //WORKSPACE의 ADMIN만 삭제가능
    @PatchMapping("/{workspaceId}/member")
    public ResponseEntity<WorkspaceMemberEditDto> editWorkspaceMemberAuthority(@PathVariable Long workspaceId, @RequestBody WorkspaceMemberEditDto requestDto) {
        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();
        WorkSpaceMember workSpaceMember = workSpaceMemberService.editAuthority(memberId, workspaceId, requestDto.authority());

        return ResponseEntity.ok(new WorkspaceMemberEditDto(workSpaceMember.getMember().getId(), workSpaceMember.getAuthority()));
    }

    @Operation(summary = "워크스페이스 멤버 추가")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')") //WORKSPACE의 ADMIN만 추가가능
    @PostMapping("/{workspaceId}/member")
    public ResponseEntity addWorkspaceMember(@PathVariable Long workspaceId, @RequestBody WorkspaceMemberEditDto requestDto) {
        workSpaceMemberService.addMember(workspaceId, requestDto.memberId(), requestDto.authority());

        return ResponseEntity.ok().build();
    }
}
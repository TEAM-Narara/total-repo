package com.narara.superboard.workspace.interfaces;

import com.narara.superboard.common.service.IAuthenticationFacade;
import com.narara.superboard.member.service.MemberService;
import com.narara.superboard.member.util.JwtTokenProvider;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceResponseDto;

import com.narara.superboard.workspace.service.WorkSpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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

    @Tag(name = "나의 워크스페이스 리스트 조회")
    @GetMapping
    public ResponseEntity<WorkSpaceListResponseDto> getWorkspaceListByMember() {
        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();

        //userId 기반, 내가 권한이 있는 워크스페이스 조회
        WorkSpaceListResponseDto workSpaceListResponseDto = new WorkSpaceListResponseDto(
                List.of(
                        new WorkSpaceResponseDto(1L, "워크스페이스1"),
                        new WorkSpaceResponseDto(2L, "워크스페이스2")
                )
        );

        return ResponseEntity.ok(workSpaceListResponseDto);
    }

    @Tag(name = "워크스페이스 생성")
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
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')")
    public ResponseEntity deleteWorkspace(@PathVariable Long workspaceId) {
//        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();
        workSpaceService.deleteWorkSpace(workspaceId);

        return ResponseEntity.ok().build();
    }

    @Tag(name = "나의 워크스페이스 리스트 조회")
    @GetMapping("/{workspaceId}")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')")
    public ResponseEntity<WorkSpaceListResponseDto> getWorkspaceListByMember(@PathVariable Long workspaceId) {
        Long memberId = authenticationFacade.getAuthenticatedUser().getUserId();

        // Sample data, replace with actual service logic
        WorkSpaceListResponseDto workSpaceListResponseDto = new WorkSpaceListResponseDto(
                List.of(
                        new WorkSpaceResponseDto(1L, "워크스페이스1"),
                        new WorkSpaceResponseDto(2L, "워크스페이스2")
                )
        );

        return ResponseEntity.ok(workSpaceListResponseDto);
    }
}

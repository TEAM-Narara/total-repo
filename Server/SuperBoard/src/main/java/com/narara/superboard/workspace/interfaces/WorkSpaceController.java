package com.narara.superboard.workspace.interfaces;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceResponseDto;
import com.narara.superboard.workspace.service.WorkSpaceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WorkSpaceController implements WorkSpaceAPI {

    private final WorkSpaceService workSpaceService;

    @Override
    public void createWorkSpace(Member member, WorkSpaceCreateRequestDto workspaceCreateRequestDto) {
        workSpaceService.createWorkSpace(member, workspaceCreateRequestDto);
    }

    @Tag(name = "나의 워크스페이스 리스트 조회")
    @GetMapping("/{workspaceId}")
    @PreAuthorize("hasPermission(#workspaceId, 'WORKSPACE', 'ADMIN')")
    public ResponseEntity<WorkSpaceListResponseDto> getWorkspaceListByMember(@PathVariable Long workspaceId) {
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

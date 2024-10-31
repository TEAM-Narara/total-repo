package com.narara.superboard.workspace.interfaces;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceListResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceResponseDto;
import com.narara.superboard.workspace.interfaces.dto.websocket.WorkspaceData;
import com.narara.superboard.workspace.service.WorkSpaceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkSpaceController implements WorkSpaceAPI {

    private final WorkSpaceService workSpaceService;

    @Tag(name = "나의 워크스페이스 리스트 조회")
    @GetMapping
    public ResponseEntity<WorkSpaceListResponseDto> getWorkspaceListByMember() {
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
    public ResponseEntity<WorkspaceData> createWorkSpace(Member member, WorkSpaceCreateRequestDto workspaceCreateRequestDto) {
        WorkSpace workSpace = workSpaceService.createWorkSpace(
                new Member(),
                new WorkSpaceCreateRequestDto("새로운 워크스페이스")
        );

        return ResponseEntity.ok(new WorkspaceData(workSpace.getId(), workSpace.getName()));
    }
}
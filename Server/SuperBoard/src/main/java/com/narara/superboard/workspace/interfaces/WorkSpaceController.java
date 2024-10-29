package com.narara.superboard.workspace.interfaces;

import com.narara.superboard.member.entity.Member;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkSpaceController implements WorkSpaceAPI {

    private final WorkSpaceService workSpaceService;

    @Override
    public void createWorkSpace(Member member, WorkSpaceCreateRequestDto workspaceCreateRequestDto) {
        workSpaceService.createWorkSpace(member, workspaceCreateRequestDto);
    }
}

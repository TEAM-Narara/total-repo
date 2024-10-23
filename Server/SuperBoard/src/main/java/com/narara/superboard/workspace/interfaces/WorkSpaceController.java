package com.narara.superboard.workspace.interfaces;

import com.narara.superboard.workspace.interfaces.dto.WorkSpaceRequestCreateDto;
import com.narara.superboard.workspace.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkSpaceController implements WorkSpaceAPI {

    private final WorkSpaceService workSpaceService;

    @Override
    public void createWorkSpace(WorkSpaceRequestCreateDto workspaceRequestCreateDto) {
        workSpaceService.createWorkSpace(workspaceRequestCreateDto);
    }
}

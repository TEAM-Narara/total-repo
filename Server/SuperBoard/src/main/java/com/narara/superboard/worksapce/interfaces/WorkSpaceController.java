package com.narara.superboard.worksapce.interfaces;

import com.narara.superboard.worksapce.interfaces.dto.CreateWorkspaceDto;
import com.narara.superboard.worksapce.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkSpaceController implements WorkSpaceAPI {

    private final WorkSpaceService workSpaceService;

    @Override
    public void createWorkSpace(CreateWorkspaceDto createWorkspaceDto) {
        workSpaceService.createWorkSpace(createWorkspaceDto);
    }
}

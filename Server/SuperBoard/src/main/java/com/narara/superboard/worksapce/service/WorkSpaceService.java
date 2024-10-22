package com.narara.superboard.worksapce.service;

import com.narara.superboard.worksapce.entity.WorkSpace;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceRequestCreateDto;
import com.narara.superboard.worksapce.interfaces.dto.WorkspaceUpdateRequestDto;

public interface WorkSpaceService {

    void createWorkSpace(WorkspaceRequestCreateDto workspaceRequestCreateDto);
    WorkSpace updateWorkSpace(Long workSpaceId, WorkspaceUpdateRequestDto workspaceUpdateRequestDto);
    WorkSpace getWorkSpace(Long workSpaceId);

}

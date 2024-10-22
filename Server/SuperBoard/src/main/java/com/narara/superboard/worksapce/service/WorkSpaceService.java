package com.narara.superboard.worksapce.service;

import com.narara.superboard.worksapce.entity.WorkSpace;
import com.narara.superboard.worksapce.interfaces.dto.CreateWorkspaceDto;

public interface WorkSpaceService {

    void createWorkSpace(CreateWorkspaceDto createWorkspaceDto);
}

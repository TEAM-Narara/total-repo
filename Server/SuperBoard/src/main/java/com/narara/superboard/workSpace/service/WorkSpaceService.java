package com.narara.superboard.workSpace.service;

import com.narara.superboard.workSpace.entity.WorkSpace;
import com.narara.superboard.workSpace.interfaces.dto.WorkSpaceDetailResponseDto;
import com.narara.superboard.workSpace.interfaces.dto.WorkSpaceRequestCreateDto;
import com.narara.superboard.workSpace.interfaces.dto.WorkSpaceUpdateRequestDto;

public interface WorkSpaceService {

    void createWorkSpace(WorkSpaceRequestCreateDto workspaceRequestCreateDto);
    WorkSpace updateWorkSpace(Long workSpaceId, WorkSpaceUpdateRequestDto workspaceUpdateRequestDto);
    void deleteWorkSpace(Long workSpaceId);
    WorkSpace getWorkSpace(Long workSpaceId);
    WorkSpaceDetailResponseDto getWorkspaceDetail(Long workSpaceId);
}

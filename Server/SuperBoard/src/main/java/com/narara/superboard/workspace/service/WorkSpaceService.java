package com.narara.superboard.workspace.service;

import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceDetailResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceUpdateRequestDto;

public interface WorkSpaceService {

    void createWorkSpace(WorkSpaceCreateRequestDto workspaceCreateRequestDto);
    WorkSpace updateWorkSpace(Long workSpaceId, WorkSpaceUpdateRequestDto workspaceUpdateRequestDto);
    void deleteWorkSpace(Long workSpaceId);
    WorkSpace getWorkSpace(Long workSpaceId);
    WorkSpaceDetailResponseDto getWorkspaceDetail(Long workSpaceId);
}

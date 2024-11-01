package com.narara.superboard.workspace.service;

import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceDetailResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceUpdateRequestDto;

import java.util.List;

public interface WorkSpaceService {
    WorkSpace createWorkSpace(Long memberId, WorkSpaceCreateRequestDto workspaceCreateRequestDto);
    WorkSpace updateWorkSpace(Long workSpaceId, WorkSpaceUpdateRequestDto workspaceUpdateRequestDto);
    void deleteWorkSpace(Long workSpaceId);
    WorkSpace getWorkSpace(Long workSpaceId);
    WorkSpaceDetailResponseDto getWorkspaceDetail(Long workSpaceId);
    List<WorkSpace> getWorkspaceByMember(Long memberId);
    WorkSpace editWorkspace(Long memberId, Long workspaceId, String name);
}

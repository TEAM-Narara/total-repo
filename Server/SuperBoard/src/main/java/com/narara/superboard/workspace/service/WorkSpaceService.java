package com.narara.superboard.workspace.service;

import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceDetailResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;

import java.util.List;

public interface WorkSpaceService {
    WorkSpaceMember createWorkSpace(Long memberId, WorkSpaceCreateRequestDto workspaceCreateRequestDto);
    void deleteWorkSpace(Long workspaceId);
    WorkSpace getWorkSpace(Long workspaceId);
    WorkSpaceDetailResponseDto getWorkspaceDetail(Long workspaceId);
    List<WorkSpace> getWorkspaceByMember(Long memberId);
    WorkSpace updateWorkSpace(Long workspaceId, String name);
}

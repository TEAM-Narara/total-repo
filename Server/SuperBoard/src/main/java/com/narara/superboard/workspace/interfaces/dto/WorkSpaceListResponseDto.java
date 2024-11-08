package com.narara.superboard.workspace.interfaces.dto;

import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record WorkSpaceListResponseDto(List<WorkSpaceResponseDto> workSpaceResponseDtoList) {
    public static WorkSpaceListResponseDto from(List<WorkSpaceMember> workspaceMemberList) {
        List<WorkSpaceResponseDto> workspaceResponse = new ArrayList<>();

        for (WorkSpaceMember workSpaceMember : workspaceMemberList) {
            workspaceResponse.add(WorkSpaceResponseDto.from(workSpaceMember));
        }

        return new WorkSpaceListResponseDto(workspaceResponse);
    }
}

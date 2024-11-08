package com.narara.superboard.workspace.interfaces.dto;

import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import lombok.Builder;

@Builder
public record WorkSpaceResponseDto(
        Long workSpaceId,
        String name,
        Authority authority
) implements WorkSpaceNameHolder {
    public static WorkSpaceResponseDto from(WorkSpaceMember workSpaceMember) {
        return new WorkSpaceResponseDto(
                workSpaceMember.getWorkSpace().getId(),
                workSpaceMember.getWorkSpace().getName(),
                workSpaceMember.getAuthority()
        );
    }
}

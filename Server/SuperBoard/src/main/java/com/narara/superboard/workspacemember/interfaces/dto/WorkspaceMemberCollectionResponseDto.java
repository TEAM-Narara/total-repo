package com.narara.superboard.workspacemember.interfaces.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record WorkspaceMemberCollectionResponseDto(
        List<WorkSpaceMemberDetailResponseDto> workspaceMemberList
) {
}

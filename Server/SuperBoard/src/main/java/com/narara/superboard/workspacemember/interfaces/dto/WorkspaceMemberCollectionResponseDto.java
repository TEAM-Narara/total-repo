package com.narara.superboard.workspacemember.interfaces.dto;

import java.util.List;

public record WorkspaceMemberCollectionResponseDto(
        List<WorkspaceMemberDetailResponseDto> workspaceMemberList
) {
}

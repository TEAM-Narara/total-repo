package com.narara.superboard.workspace.interfaces.dto;

import lombok.Builder;

@Builder
public record WorkSpaceResponseDto(
        Long workSpaceId,
        String name
) implements WorkSpaceNameHolder {
}

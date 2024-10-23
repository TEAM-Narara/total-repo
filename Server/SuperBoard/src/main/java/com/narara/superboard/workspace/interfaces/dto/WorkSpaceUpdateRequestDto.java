package com.narara.superboard.workspace.interfaces.dto;

import lombok.Builder;

@Builder
public record WorkSpaceUpdateRequestDto(
        //        String description,
        String name) implements WorkSpaceNameHolder {
}

package com.narara.superboard.worksapce.interfaces.dto;

import lombok.Builder;

@Builder
public record WorkspaceUpdateRequestDto(
        //        String description,
        String name) implements WorkspaceNameHolder {
}

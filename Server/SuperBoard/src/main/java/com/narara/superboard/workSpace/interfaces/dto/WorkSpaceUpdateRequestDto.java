package com.narara.superboard.workSpace.interfaces.dto;

import lombok.Builder;

@Builder
public record WorkSpaceUpdateRequestDto(
        //        String description,
        String name) implements WorkSpaceNameHolder {
}

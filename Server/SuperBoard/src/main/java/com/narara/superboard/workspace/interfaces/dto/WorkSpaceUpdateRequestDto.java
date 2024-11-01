package com.narara.superboard.workspace.interfaces.dto;

import lombok.Builder;

@Builder
public record WorkSpaceUpdateRequestDto(String name) implements WorkSpaceNameHolder {
}

package com.narara.superboard.worksapce.interfaces.dto;

import lombok.Builder;

@Builder
public record WorkspaceUpdateRequestDto(String name, String description) implements WorkspaceNameHolder {
}

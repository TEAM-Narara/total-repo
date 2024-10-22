package com.narara.superboard.worksapce.interfaces.dto;

public record WorkspaceUpdateRequestDto(String name, String description) implements WorkspaceNameHolder {
}

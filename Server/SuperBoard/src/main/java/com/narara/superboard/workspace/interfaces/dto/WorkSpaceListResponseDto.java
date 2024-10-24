package com.narara.superboard.workspace.interfaces.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record WorkSpaceListResponseDto(List<WorkSpaceResponseDto> workSpaceResponseDtoList) {
}

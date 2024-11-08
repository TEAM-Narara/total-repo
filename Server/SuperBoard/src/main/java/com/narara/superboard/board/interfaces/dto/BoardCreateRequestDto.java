package com.narara.superboard.board.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.CoverDto;

public record BoardCreateRequestDto(
        Long workspaceId,
        String name,
        String visibility,
        CoverDto cover,
        boolean isClosed) implements BoardCoreHolder {
}

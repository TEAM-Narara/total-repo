package com.narara.superboard.board.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.CoverDto;

public record BoardCreateRequestDto(
        Long workspaceId,
        String name,
        String visibility,
        CoverDto cover,
        boolean isClosed // TODO: 생성 시, 보드 close 여부를 넣는게 맞는가?
) implements BoardCoreHolder {
}

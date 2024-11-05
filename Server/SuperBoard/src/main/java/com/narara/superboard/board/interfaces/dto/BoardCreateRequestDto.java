package com.narara.superboard.board.interfaces.dto;

import java.util.Map;

public record BoardCreateRequestDto(
        Long workSpaceId,
        String name,
        String visibility,
        CoverDto background) implements BoardCoreHolder {
}

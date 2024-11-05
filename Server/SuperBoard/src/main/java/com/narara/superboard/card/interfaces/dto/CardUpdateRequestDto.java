package com.narara.superboard.card.interfaces.dto;

import com.narara.superboard.board.interfaces.dto.BoardBackgroundDto;
import com.narara.superboard.common.interfaces.dto.CoverHolder;

public record CardUpdateRequestDto(
        String name,
        String description,
        Long startAt,
        Long endAt,
        BoardBackgroundDto cover // 배경 (JSON: type과 value)
) implements CoverHolder {
}
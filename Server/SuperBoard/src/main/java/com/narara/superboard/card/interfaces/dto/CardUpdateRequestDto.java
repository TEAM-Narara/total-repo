package com.narara.superboard.card.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.CoverHolder;
import java.util.Map;

public record CardUpdateRequestDto(
        String name,
        String description,
        Long startAt,
        Long endAt,
        Map<String, Object> cover // 배경 (JSON: type과 value)
) implements CoverHolder {
}

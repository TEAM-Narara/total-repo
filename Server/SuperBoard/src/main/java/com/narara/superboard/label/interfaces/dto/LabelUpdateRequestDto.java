package com.narara.superboard.label.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.ColorHolder;

public record LabelUpdateRequestDto(
        String name,
        Long color
) implements ColorHolder {
}

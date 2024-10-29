package com.narara.superboard.label.interfaces.dto;

import com.narara.superboard.common.interfaces.dto.ColorHolder;

public record LabelCreateRequestDto(
        String name,
        Long color
) implements ColorHolder {
}

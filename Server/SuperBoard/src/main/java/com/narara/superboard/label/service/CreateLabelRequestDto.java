package com.narara.superboard.label.service;

import com.narara.superboard.common.interfaces.dto.ColorHolder;

public record CreateLabelRequestDto(
        String name,
        Long color
) implements ColorHolder {
}

package com.narara.superboard.label.interfaces.dto;

import com.narara.superboard.label.entity.Label;
import lombok.Builder;

@Builder
public record LabelSimpleResponseDto(
        Long labelId,
        Long boardId,
        String labelName,
        Long labelColor
) {
    public static LabelSimpleResponseDto of(Label label){
        return LabelSimpleResponseDto.builder()
                .labelId(label.getId())
                .boardId(label.getBoard().getId())
                .labelName(label.getName())
                .labelColor(label.getColor())
                .build();
    }
}

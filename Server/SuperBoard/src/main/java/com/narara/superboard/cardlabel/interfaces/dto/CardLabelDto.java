package com.narara.superboard.cardlabel.interfaces.dto;

import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.label.entity.Label;
import lombok.Builder;

@Builder
public record CardLabelDto(Long cardLabelId, Long labelId, String name, Long color, Boolean IsActivated) {
    public static CardLabelDto of(CardLabel cardLabel){
        Label label = cardLabel.getLabel();
        return CardLabelDto.builder()
                .cardLabelId(cardLabel.getId())
                .labelId(label.getId())
                .name(label.getName())
                .color(label.getColor())
                .IsActivated(cardLabel.getIsActivated())
                .build();
    }
}


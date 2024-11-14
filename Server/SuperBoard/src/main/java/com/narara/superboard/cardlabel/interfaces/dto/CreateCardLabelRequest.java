package com.narara.superboard.cardlabel.interfaces.dto;

public record CreateCardLabelRequest(Long cardId, Long labelId,Boolean isActivated) {
}

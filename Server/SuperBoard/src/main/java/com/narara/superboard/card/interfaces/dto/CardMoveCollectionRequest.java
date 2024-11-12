package com.narara.superboard.card.interfaces.dto;

import java.util.List;

public record CardMoveCollectionRequest(List<CardMoveRequest> moveRequest) {
}

package com.narara.superboard.card.interfaces.dto;

import java.util.List;

public sealed interface CardMoveResult permits CardMoveResult.SingleCardMove, CardMoveResult.ReorderedCardMove {

    record SingleCardMove(CardMoveResponseDto orderInfo) implements CardMoveResult {}

    record ReorderedCardMove(List<CardMoveResponseDto> orderInfos) implements CardMoveResult {}
}

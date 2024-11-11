package com.narara.superboard.card.interfaces.dto;

import com.narara.superboard.card.interfaces.dto.CardMoveResult.DeletedCardMove;
import com.narara.superboard.card.interfaces.dto.CardMoveResult.ReorderedCardMove;
import com.narara.superboard.card.interfaces.dto.CardMoveResult.SingleCardMove;
import java.util.List;

public sealed interface CardMoveResult permits DeletedCardMove, ReorderedCardMove, SingleCardMove {

    record SingleCardMove(CardMoveResponseDto orderInfo) implements CardMoveResult {}

    record ReorderedCardMove(List<CardMoveResponseDto> orderInfos) implements CardMoveResult {}

    record DeletedCardMove(Long listId) implements CardMoveResult {} // 삭제된 리스트의 ID를 포함
}

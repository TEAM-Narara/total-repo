package com.narara.superboard.list.interfaces.dto;

public sealed interface ListMoveResult permits ListMoveResult.SingleListMove, ListMoveResult.ReorderedListMove {

    record SingleListMove(ListMoveResponseDto orderInfo) implements ListMoveResult {}

    record ReorderedListMove(java.util.List<ListMoveResponseDto> orderInfos) implements ListMoveResult {}
}

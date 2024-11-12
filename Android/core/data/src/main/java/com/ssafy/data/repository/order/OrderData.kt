package com.ssafy.data.repository.order

data class ListMoveResponseDto(val listId: Long, val myOrder: Long)

sealed class ListMoveResult {
    data class SingleListMove(val response: ListMoveResponseDto) : ListMoveResult()
    data class ReorderedListMove(val responses: List<ListMoveResponseDto>) : ListMoveResult()
}

object MoveConst {
    const val DEFAULT_TOP_ORDER: Long = 4000000000000000000L // 초기 순서 값
    const val MOVE_TOP_ORDER_RATIO: Double = 2.0 / 3.0
    const val MOVE_BOTTOM_ORDER_RATIO: Double = 1.0 / 3.0
    const val LARGE_INCREMENT: Long = 50000000000000000L

    const val MAX_INSERTION_DISTANCE_FOR_FIXED_GAP: Long = 10000000000000000L
    const val HALF_DIVIDER: Long = 2

    const val REORDER_GAP: Long = 10000000000000000L // 각 카드 사이 간
}

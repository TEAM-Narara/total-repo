package com.ssafy.data.socket.board.model.card

data class MoveCardRequestDto(
    val boardId: Long,
    val listId: Long,
    val updatedCard: List<MovedCard>
)

data class MovedCard(
    val cardId: Long,
    val movedListId: Long,
    val myOrder: Long
)

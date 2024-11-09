package com.ssafy.data.socket.board.model.card

data class DeleteCardRequestDto(
    val cardId: Long,
    val listId: Long,
    val isDeleted: Boolean,
    val isArchived: Boolean
)
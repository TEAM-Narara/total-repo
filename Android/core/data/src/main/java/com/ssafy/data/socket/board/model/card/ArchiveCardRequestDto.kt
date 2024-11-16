package com.ssafy.data.socket.board.model.card

data class ArchiveCardRequestDto(
    val cardId: Long,
    val listId: Long,
    val isArchived: Boolean
)
package com.ssafy.data.socket.board.model.card

data class EditCardRequestDto(
    val cardId: Long,
    val listId: Long,
    val name: String,
    val description: String,
    val startAt: Long?,
    val endAt: Long?,
    val coverType: String,
    val coverValue: String,
    val isDeleted: Boolean,
    val isArchived: Boolean
)
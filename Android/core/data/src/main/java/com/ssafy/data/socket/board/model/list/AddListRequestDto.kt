package com.ssafy.data.socket.board.model.list

data class AddListRequestDto(
    val listId: Long,
    val boardId: Long,
    val name: String,
    val myOrder: Long,
    val isArchived: Boolean,
    val isDeleted: Boolean
)

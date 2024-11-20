package com.ssafy.data.socket.board.model.list

data class MoveListRequestDto(
    val boardId: Long,
    val updatedList: List<MovedList>
)

data class MovedList(
    val listId: Long,
    val myOrder: Long
)

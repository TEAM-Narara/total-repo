package com.ssafy.data.socket.board.model.board

data class EditBoardLabelRequestDto(
    val boardId: Long,
    val labelId: Long,
    val name: String,
    val color: Long
)
package com.ssafy.data.socket.board.model.board

data class DeleteBoardLabelRequestDto(
    val boardId: Long,
    val labelId: Long,
)
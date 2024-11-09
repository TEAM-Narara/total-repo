package com.ssafy.data.socket.board.model

data class EditBoardWatchRequestDto(
    val boardId: Long,
    val memberId: Long,
    val isAlert: Boolean
)
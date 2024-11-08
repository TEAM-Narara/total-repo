package com.ssafy.data.socket.board.model

data class EditBoardArchiveRequestDto(
    val boardId: Long,
    val isArchive: Boolean
)
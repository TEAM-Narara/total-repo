package com.ssafy.data.socket.board.model.reply

data class DeleteReplyRequestDto(
    val cardId: Long,
    val replyId: Long,
    val isDeleted: Boolean,
)
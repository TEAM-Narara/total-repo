package com.ssafy.data.socket.board.model.reply

data class EditReplyRequestDto(
    val cardId: Long,
    val replyId: Long,
    val memberId: Long,
    val content: String,
    val isDeleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)
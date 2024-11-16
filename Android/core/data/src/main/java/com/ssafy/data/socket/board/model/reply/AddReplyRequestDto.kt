package com.ssafy.data.socket.board.model.reply

data class AddReplyRequestDto(
    val cardId: Long,
    val replyId: Long,
    val memberId: Long,
    val content: String,
    val isDeleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)
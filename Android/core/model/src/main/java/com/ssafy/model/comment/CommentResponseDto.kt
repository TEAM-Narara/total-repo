package com.ssafy.model.comment

data class CommentResponseDto(
    val cardId: Long,
    val replyId: Long,
    val content: String,
    val memberId: Long,
    val isDeleted: Boolean,
    val lastUpdatedAt: Long,
)

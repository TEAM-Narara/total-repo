package com.ssafy.data.socket.board.model.attachment

data class EditCardAttachmentCoverRequestDto(
    val cardId: Long,
    val attachmentId: Long,
    val imgURL: String,
    val type: String,
    val isCover: Boolean,
    val createdAt: Long
)
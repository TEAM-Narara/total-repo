package com.ssafy.model.attachment

data class AttachmentResponseDto(
    val attachmentId: Long,
    val imgURL: String,
    val type: String,
    val isCover: Boolean,
    val createAt: Long
)

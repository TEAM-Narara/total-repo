package com.ssafy.model.with

data class AttachmentDTO(
    val id: Long = 0L,
    val cardId: Long = 0L,
    val url: String = "",
    val type: String = "",
    val isCover: Boolean = false,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)

package com.ssafy.model.with

data class ReplyDTO(
    val id: Long = 0L,
    val cardId: Long = 0L,
    val memberId: Long = 0L,
    val content: String = "",
    val createAt: Long = 0L,
    val updateAt: Long = 0L,

    @Transient
    val isStatus: String = "STAY"
)

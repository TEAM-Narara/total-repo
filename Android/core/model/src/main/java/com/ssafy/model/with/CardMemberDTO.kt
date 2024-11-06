package com.ssafy.model.with

data class CardMemberDTO(
    val id: Long = 0L,
    val memberId: Long = 0L,
    val cardId: Long = 0L,
    val isRepresentative: Boolean = false,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)

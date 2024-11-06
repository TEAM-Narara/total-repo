package com.ssafy.model.with

data class CardMemberAlarmDTO(
    val cardId: Long = 0L,
    val isAlert: Boolean = false,

    @Transient
    val isStatus: String = "STAY"
)

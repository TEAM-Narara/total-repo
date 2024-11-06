package com.ssafy.model.with

data class BoardMemberAlarmDTO(
    val boardId: Long = 0L,
    val isAlert: Boolean = false,

    @Transient
    val isStatus: String = "STAY"
)

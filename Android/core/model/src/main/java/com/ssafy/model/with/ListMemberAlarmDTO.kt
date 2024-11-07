package com.ssafy.model.with

data class ListMemberAlarmDTO(
    val listId: Long = 0L,
    val isAlert: Boolean = false,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)

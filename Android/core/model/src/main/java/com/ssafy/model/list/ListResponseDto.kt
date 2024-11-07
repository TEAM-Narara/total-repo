package com.ssafy.model.list

import com.ssafy.model.with.DataStatus

data class ListResponseDto (
    val boardId: Long,
    val listId: Long,
    val myOrder: Long = 0L,
    val isArchived: Boolean = false,
    val name: String,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)
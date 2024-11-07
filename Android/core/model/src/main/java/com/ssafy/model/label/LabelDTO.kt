package com.ssafy.model.label

import com.ssafy.model.with.DataStatus

data class LabelDTO(
    val id: Long = 0L,
    val boardId: Long = 0L,
    val name: String = "",
    val color: Long = 0L,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)

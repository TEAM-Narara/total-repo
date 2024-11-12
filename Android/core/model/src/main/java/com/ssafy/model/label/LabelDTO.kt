package com.ssafy.model.label

import com.ssafy.model.with.DataStatus

data class LabelDTO(
    val labelId: Long = 0L,
    val boardId: Long = 0L,
    val labelName: String = "",
    val labelColor: Long = 0L,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)

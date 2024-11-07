package com.ssafy.model.background

import com.ssafy.model.with.DataStatus

data class BackgroundDto(
    val color: Long,
    val imgPath: String,
    val id: Long = 0,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)

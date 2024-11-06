package com.ssafy.model.background

import com.ssafy.model.with.DataStatus

data class BackgroundDto(
    val id: Long,
    val imgPath: String,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)
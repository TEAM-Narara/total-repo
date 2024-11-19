package com.ssafy.model.card

import com.ssafy.model.background.Cover
import com.ssafy.model.with.DataStatus

data class CardResponseDto(
    val id: Long = 0L,
    val listId: Long = 0L,
    val name: String = "",
    val description: String? = null,
    val startAt: Long? = null,
    val endAt: Long? = null,
    val cover: Cover? = null,
    val myOrder: Long = 0L,
    val isArchived: Boolean = false,

    @Transient val isStatus: DataStatus = DataStatus.STAY,
    @Transient val columnUpdate: Long = 0L,
)

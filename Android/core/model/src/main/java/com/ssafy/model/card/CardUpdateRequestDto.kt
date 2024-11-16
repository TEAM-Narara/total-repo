package com.ssafy.model.card

import com.ssafy.model.background.Cover

data class CardUpdateRequestDto(
    val name: String,
    val description: String?,
    val startAt: Long?,
    val endAt: Long?,
    val cover: Cover,
)

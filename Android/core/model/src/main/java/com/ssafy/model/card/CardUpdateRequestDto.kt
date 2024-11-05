package com.ssafy.model.card

data class CardUpdateRequestDto(
    val name: String,
    val description: String,
    val startAt: Long,
    val endAt: Long,
    val cover: Any,
)

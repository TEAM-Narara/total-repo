package com.ssafy.model.card

data class CardDetailDto(
    val cardSimpleResponseDto: CardDTO,
    val description: String,
    val startAt: Long,
    val endAt: Long,
    val myOrder: Long,
    val isArchived: Boolean,
    val coverType: String,
    val coverValue: String,
)

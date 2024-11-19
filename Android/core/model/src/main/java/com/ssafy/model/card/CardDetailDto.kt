package com.ssafy.model.card

import com.ssafy.model.with.CoverType

data class CardDetailDto(
    val cardSimpleResponseDto: CardDTO,
    val description: String,
    val startAt: Long,
    val endAt: Long,
    val myOrder: Long,
    val isArchived: Boolean,
    val coverType: CoverType,
    val coverValue: String,
)

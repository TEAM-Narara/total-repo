package com.ssafy.model.card

data class CardLabelUpdateDto(
    val id: Long = 0L,
    val labelId: Long = 0L,
    val cardId: Long = 0L,
    val isActivated: Boolean = true,
)

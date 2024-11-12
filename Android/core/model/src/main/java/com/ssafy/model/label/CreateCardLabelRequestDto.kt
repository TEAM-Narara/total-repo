package com.ssafy.model.label

data class CreateCardLabelRequestDto(
    val cardId: Long,
    val labelId: Long,
)
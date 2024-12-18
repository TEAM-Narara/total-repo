package com.ssafy.model.with

data class CardLabelDTO(
    val cardLabelId: Long = 0L,
    val labelId: Long = 0L,
    val cardId: Long = 0L,
    val isActivated: Boolean = true,

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)

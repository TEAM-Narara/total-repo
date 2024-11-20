package com.ssafy.model.with

data class CardLabelWithLabelDTO(
    val cardLabelId: Long = 0L,
    val labelId: Long = 0L,
    val cardId: Long = 0L,
    val isActivated: Boolean = true,
    val cardLabelStatus: DataStatus = DataStatus.STAY,

    val labelBoardId: Long = 0L,
    val labelName: String = "",
    val labelColor: Long = 0L,
    val labelStatus: DataStatus = DataStatus.STAY
)
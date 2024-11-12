package com.ssafy.model.with

data class LabelWithCardLabelDTO(
    val labelId: Long = 0L,
    val boardId: Long = 0L,
    val labelName: String = "",
    val labelColor: Long = 0L,

    val isActivated: Boolean = false,
)
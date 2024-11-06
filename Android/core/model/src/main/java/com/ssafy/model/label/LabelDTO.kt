package com.ssafy.model.label

data class LabelDTO(
    val id: Long = 0L,
    val boardId: Long = 0L,
    val name: String = "",
    val color: Long = 0L,

    @Transient
    val isStatus: String = "STAY"
)

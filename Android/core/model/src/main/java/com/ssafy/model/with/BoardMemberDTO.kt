package com.ssafy.model.with

data class BoardMemberDTO(
    val id: Long = 0L,
    val boardId: Long = 0L,
    val memberId: Long = 0L,
    val authority: String = "",

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)

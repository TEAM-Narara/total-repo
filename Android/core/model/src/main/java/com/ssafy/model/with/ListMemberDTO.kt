package com.ssafy.model.with

data class ListMemberDTO(
    val id: Long = 0L,
    val memberId: Long = 0L,
    val listId: Long = 0L,

    @Transient
    val isStatus: String = "STAY"
)

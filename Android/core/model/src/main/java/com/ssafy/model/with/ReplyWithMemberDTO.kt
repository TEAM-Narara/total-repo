package com.ssafy.model.with

data class ReplyWithMemberDTO(
    val id: Long = 0L,
    val cardId: Long = 0L,
    val memberId: Long = 0L,
    val content: String = "",
    val createAt: Long = 0L,
    val updateAt: Long = 0L,

    val memberEmail: String = "",
    val memberNickname: String = "",
    val memberProfileImgUrl: String? = "",

    @Transient
    val isStatus: DataStatus = DataStatus.STAY
)
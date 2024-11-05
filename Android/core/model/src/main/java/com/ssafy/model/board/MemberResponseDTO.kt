package com.ssafy.model.board

data class MemberResponseDTO(
    val memberId: Long,
    val authority: String,
    val memberEmail: String,
    val memberNickname: String,
    val memberProfileImgUrl: String
)

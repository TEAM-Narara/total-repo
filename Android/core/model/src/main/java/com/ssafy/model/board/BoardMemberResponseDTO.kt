package com.ssafy.model.board

data class BoardMemberResponseDTO(
    val memberId: Long,
    val authority: String,
    val memberEmail: String,
    val memberNickname: String,
    val memberProfileImgUrl: String
)

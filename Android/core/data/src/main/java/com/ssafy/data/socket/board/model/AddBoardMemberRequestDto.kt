package com.ssafy.data.socket.board.model

data class AddBoardMemberRequestDto(
    val boardMemberId: Long,
    val boardId: Long,
    val memberId: Long,
    val memberEmail: String,
    val memberNickname: String,
    val memberProfileImgUrl: String,
    val authority: String,
    val isDeleted: Boolean
)
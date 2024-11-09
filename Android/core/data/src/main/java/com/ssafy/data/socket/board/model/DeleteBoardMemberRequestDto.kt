package com.ssafy.data.socket.board.model

data class DeleteBoardMemberRequestDto(
    val boardMemberId: Long,
    val boardId: Long,
    val memberId: Long,
    val memberEmail: String,
    val memberName: String,
    val profileImgUrl: String,
    val authority: String,
    val isDeleted: Boolean
)
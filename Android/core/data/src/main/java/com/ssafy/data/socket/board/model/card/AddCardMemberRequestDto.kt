package com.ssafy.data.socket.board.model.card

data class AddCardMemberRequestDto(
    val cardMemberId: Long,
    val cardId: Long,
    val memberId: Long,
    val isAlert: Boolean
)
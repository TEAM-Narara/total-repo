package com.ssafy.data.socket.board.model.card

data class DeleteCardMemberRequestDto(
    val cardMemberId: Long,
    val cardId: Long,
    val memberId: Long,
    val isAlert: Boolean
)
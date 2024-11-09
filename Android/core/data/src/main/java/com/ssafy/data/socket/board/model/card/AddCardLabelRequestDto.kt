package com.ssafy.data.socket.board.model.card

data class AddCardLabelRequestDto(
    val cardLabelId: Long,
    val labelId: Long,
    val cardId: Long,
    val isActivated: Boolean
)
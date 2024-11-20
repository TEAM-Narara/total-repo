package com.ssafy.data.socket.board.model.card

data class DeleteCardLabelRequestDto(
    val cardLabelId: Long,
    val labelId: Long,
    val cardId: Long,
    val isActivated: Boolean
)
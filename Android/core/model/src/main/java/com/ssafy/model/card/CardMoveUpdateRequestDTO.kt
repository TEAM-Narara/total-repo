package com.ssafy.model.card

data class CardMoveUpdateListRequestDTO(
    val moveRequest: List<CardMoveUpdateRequestDTO>
)

data class CardMoveUpdateRequestDTO(
    val cardId: Long,
    val myOrder: Long
)

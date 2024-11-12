package com.ssafy.model.with

data class CardWithListAndBoardNameDTO(
    val cardId: Long,
    val cardName: String,
    val listName: String,
    val boardName: String
)

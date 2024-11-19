package com.ssafy.database.dto.with

data class CardWithListAndBoardName(
    val cardId: Long,
    val cardName: String,
    val listName: String,
    val boardName: String
)

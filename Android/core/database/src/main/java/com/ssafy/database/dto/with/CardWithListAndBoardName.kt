package com.ssafy.database.dto.with

import androidx.room.Embedded
import com.ssafy.database.dto.Card

data class CardWithListAndBoardName(
    val cardId: Long,
    val cardName: String,
    val listName: String,
    val boardName: String
)

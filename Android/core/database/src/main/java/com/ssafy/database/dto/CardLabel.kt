package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "card_label",
    indices = [Index(value = ["cardId"]), Index(value = ["labelId"])]
)
data class CardLabel(
    @PrimaryKey val id: Long = 0L,
    val labelId: Long = 0L,
    val cardId: Long = 0L,
    val isActivated: Boolean = true,

    val isStatus: String = "STAY"
)

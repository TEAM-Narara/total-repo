package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_label")
data class CardLabel(
    @PrimaryKey val id: Long = 0L,
    val labelId: Long = 0L,
    val cardId: Long = 0L,
    val isActivated: Boolean = true
)

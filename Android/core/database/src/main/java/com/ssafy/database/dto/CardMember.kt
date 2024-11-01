package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_member")
data class CardMember(
    @PrimaryKey val id: Long = 0L,
    val memberId: Long = 0L,
    val cardId: Long = 0L,
    val isAlert: Boolean = false
)

package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "card_member",
    indices = [Index(value = ["cardId"]), Index(value = ["memberId"])]
)
data class CardMember(
    @PrimaryKey val id: Long = 0L,
    val memberId: Long = 0L,
    val cardId: Long = 0L,
    val isRepresentative: Boolean = false,

    val isStatus: String = "STAY"
)

// 내 카드들의 알람
@Entity(tableName = "card_member_alarm",
    primaryKeys = ["cardId"])
data class CardMemberAlarm(
    val cardId: Long = 0L,
    val isAlert: Boolean = false,

    val isStatus: String = "STAY"
)

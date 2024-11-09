package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.model.with.DataStatus

@Entity(tableName = "card_member", primaryKeys = ["memberId", "cardId"])
data class CardMemberEntity(
    val id: Long = 0L,
    val memberId: Long = 0L,
    val cardId: Long = 0L,
    val isRepresentative: Boolean = false,

    val isStatus: DataStatus = DataStatus.STAY
)

// 내 카드들의 알람
@Entity(tableName = "card_member_alarm")
data class CardMemberAlarmEntity(
    @PrimaryKey val cardId: Long = 0L,
    val isAlert: Boolean = true,

    val isStatus: DataStatus = DataStatus.STAY
)

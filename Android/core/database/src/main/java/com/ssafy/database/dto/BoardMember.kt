package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

// 보드에 연관된 모든 사용자
@Entity(tableName = "board_member")
data class BoardMember(
    @PrimaryKey val id: Long = 0L,
    val memberId: Long = 0L,
    val boardId: Long = 0L,
    val authority: String = "",
)

// 내 보드들의 알람
@Entity(tableName = "board_member_alarm",
    primaryKeys = ["boardId"])
data class BoardMemberAlarm(
    val boardId: Long = 0L,
    val isAlert: Boolean = false
)

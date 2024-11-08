package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.model.member.Authority
import com.ssafy.model.with.DataStatus

// 보드에 연관된 모든 사용자
@Entity(tableName = "board_member")
data class BoardMemberEntity(
    @PrimaryKey val id: Long = 0L,
    val memberId: Long = 0L,
    val boardId: Long = 0L,
    val authority: Authority = Authority.MEMBER,

    val isStatus: DataStatus = DataStatus.STAY,
)

// 내 보드들의 알람
@Entity(tableName = "board_member_alarm")
data class BoardMemberAlarmEntity(
    @PrimaryKey val boardId: Long = 0L,
    val isAlert: Boolean = false,

    val isStatus: DataStatus = DataStatus.STAY
)

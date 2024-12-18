package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.model.with.DataStatus

@Entity(tableName = "list_member", primaryKeys = ["memberId", "listId"])
data class ListMemberEntity(
    val id: Long = 0L,
    val memberId: Long = 0L,
    val listId: Long = 0L,

    val isStatus: DataStatus = DataStatus.STAY
)

// 내 리스트들의 알람
@Entity(tableName = "list_member_alarm")
data class ListMemberAlarmEntity(
    @PrimaryKey val listId: Long = 0L,
    val isAlert: Boolean = true,

    val isStatus: DataStatus = DataStatus.STAY
)

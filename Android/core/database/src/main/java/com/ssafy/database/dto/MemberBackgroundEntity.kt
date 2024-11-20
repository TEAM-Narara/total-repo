package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.model.with.DataStatus

@Entity(tableName = "member_background")
data class MemberBackgroundEntity(
    @PrimaryKey val id: Long = 0L,
    val url: String,

    val isStatus: DataStatus = DataStatus.STAY
)

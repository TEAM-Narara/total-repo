package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_background")
data class MemberBackground(
    @PrimaryKey val id: Long = 0L,
    val memberId: Long = 0L,
    val url: String? = null,
)

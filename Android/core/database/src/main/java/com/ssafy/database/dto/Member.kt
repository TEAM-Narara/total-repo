package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member")
data class Member(
    @PrimaryKey val id: Long = 0L,
    val email: String = "",
    val nickname: String = "",
    val profileImageUrl: String = "",
)

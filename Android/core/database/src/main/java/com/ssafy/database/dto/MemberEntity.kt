package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ssafy.model.with.DataStatus

@Entity(tableName = "member",
        indices = [Index(value = ["email"], unique = true)])
data class MemberEntity(
    @PrimaryKey val id: Long = 0L,
    val email: String = "",
    val nickname: String = "",
    val profileImageUrl: String = "",
)

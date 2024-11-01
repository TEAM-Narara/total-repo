package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "board_member")
data class BoardMember(
    @PrimaryKey val id: Long = 0L,
    val memberId: Long = 0L,
    val boardId: Long = 0L,
    val authority: String = "",
    val isAlert: Boolean = false
)

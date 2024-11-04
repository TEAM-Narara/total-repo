package com.ssafy.database.dto

import androidx.room.Entity

@Entity(tableName = "board_activity")
data class BoardActivity(
    val id: Long = 0L,
    val boardId: Long = 0L,
    val content: String = ""
)

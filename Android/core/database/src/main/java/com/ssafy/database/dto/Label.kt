package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "label")
data class Label(
    @PrimaryKey val id: Long = 0L,
    val boardId: Long = 0L,
    val name: String = "",
    val color: Long = 0L,

    val isStatus: String = "STAY"
)

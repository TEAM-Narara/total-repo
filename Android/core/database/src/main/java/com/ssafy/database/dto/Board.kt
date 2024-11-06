package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "board")
data class Board(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val workspaceId: Long = 0L,
    val name: String = "",
    val backgroundType: String? = null,
    val backgroundValue: String? = null,
    val visibility: String = "",
    val isClosed: Boolean = false,

//    val lastListOrder: Long = 0L,
//    val offset: Long = 0L,
//    val version: Long = 0L

    val isStatus: String = "STAY",
    val columnUpdate: Long = 0L,
)
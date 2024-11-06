package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reply")
data class Reply(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val cardId: Long = 0L,
    val memberId: Long = 0L,
    val content: String = "",
    val createAt: Long = 0L,
    val updateAt: Long = 0L,

    val isStatus: String = "STAY"
)

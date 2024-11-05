package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "attachment")
data class Attachment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val cardId: Long = 0L,
    val url: String = "",
    val type: String = "",
    val isCover: Boolean = false,

    val isStatus: String = "STAY"
)

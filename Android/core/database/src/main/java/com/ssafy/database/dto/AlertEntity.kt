package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alert")
data class AlertEntity(
    @PrimaryKey val id: Long = 0L,
    val memberId: Long = 0L,
    val title: String = "",
    val body: String? = null,
    val createAt: Long = 0L,
    val updateAt: Long = 0L,
)

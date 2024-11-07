package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.model.with.DataStatus

@Entity(tableName = "label")
data class LabelEntity(
    @PrimaryKey val id: Long = 0L,
    val boardId: Long = 0L,
    val name: String = "",
    val color: Long = 0L,

    val isStatus: DataStatus = DataStatus.STAY
)

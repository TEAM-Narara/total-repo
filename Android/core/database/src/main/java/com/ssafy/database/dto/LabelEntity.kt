package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.database.dto.bitmask.BitPosition
import com.ssafy.model.with.DataStatus

@Entity(tableName = "label")
data class LabelEntity(
    @PrimaryKey val id: Long = 0L,
    val boardId: Long = 0L,
    @BitPosition(0) val name: String = "",
    @BitPosition(1) val color: Long = 0L,

    val isStatus: DataStatus = DataStatus.STAY,
    val columnUpdate: Long = 0L
)

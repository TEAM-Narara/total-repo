package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.database.dto.bitmask.BitPosition
import com.ssafy.model.with.DataStatus

@Entity(tableName = "board")
data class BoardEntity(
    @PrimaryKey(autoGenerate = false) val id: Long = 0L,
    val workspaceId: Long = 0L,
    @BitPosition(0) val name: String = "",
    @BitPosition(1) val coverType: String?,
    @BitPosition(2) val coverValue: String?,
    @BitPosition(3) val visibility: String = "",
    @BitPosition(4) val isClosed: Boolean = false,

//    val lastListOrder: Long = 0L,
//    val offset: Long = 0L,
//    val version: Long = 0L

    val isStatus: DataStatus = DataStatus.STAY,
    val columnUpdate: Long = 0L,
)

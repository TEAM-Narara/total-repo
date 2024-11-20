package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.database.dto.bitmask.BitPosition
import com.ssafy.model.with.DataStatus

@Entity(tableName = "list")
data class ListEntity(
    @PrimaryKey(autoGenerate = false)  val id: Long = 0L,
    val boardId: Long = 0L,
    @BitPosition(0) val name: String = "",
    @BitPosition(1) val myOrder: Long = 0L,
    @BitPosition(2) val isArchived: Boolean = false,

//    val version: Long = 0L,

    val isStatus: DataStatus = DataStatus.STAY,
    val lastCardOrder: Long = 4000000000000000000L,
    val columnUpdate: Long = 0L
)

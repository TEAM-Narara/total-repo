package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.model.with.DataStatus

@Entity(tableName = "list")
data class ListEntity(
    @PrimaryKey(autoGenerate = false)  val id: Long = 0L,
    val boardId: Long = 0L,
    val name: String = "",
    val myOrder: Long = 0L,
    val isArchived: Boolean = false,

//    val lastCardOrder: Long = 0L,
//    val version: Long = 0L,

    val isStatus: DataStatus = DataStatus.STAY,
    val columnUpdate: Long = 0L
)

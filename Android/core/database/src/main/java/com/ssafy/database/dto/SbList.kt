package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "list",
    indices = [Index(value = ["boardId"])]
)
data class SbList(
    @PrimaryKey(autoGenerate = true)  val id: Long = 0L,
    val boardId: Long = 0L,
    val name: String = "",
    val myOrder: Long = 0L,
    val isArchived: Boolean = false,

//    val lastCardOrder: Long = 0L,
//    val version: Long = 0L,

    val isStatus: String = "STAY",
    val columnUpdate: Long = 0L
)

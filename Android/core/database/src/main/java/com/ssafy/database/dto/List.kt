package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list")
data class List(
    @PrimaryKey(autoGenerate = true)  val id: Long = 0L,
    val boardId: Long = 0L,
    val name: String = "",
    val myOrder: Long = 0L,
    val lastCardOrder: Long = 0L,
    val isArchived: Boolean = false,
    val isStatus: String = "STAY",
    val columnUpdate: Long = 0L,
    val version: Long = 0L
)

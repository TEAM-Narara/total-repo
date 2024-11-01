package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card")
data class Card(
    @PrimaryKey(autoGenerate = true)  val id: Long = 0L,
    val listId: Long = 0L,
    val name: String = "",
    val description: String? = null,
    val startAt: Long? = null,
    val endAt: Long? = null,
    // TODO JSONB
    val cover: String? = null,
    val myOrder: Long = 0L,
    val isArchived: Boolean = false,
    val isStatus: String = "STAY",
    val columnUpdate: Long = 0L,
)

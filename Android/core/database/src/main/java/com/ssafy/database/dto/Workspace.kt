package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workspace")
data class Workspace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String = "",
//    val offset: Long = 0L,

    val isStatus: String = "STAY"
)

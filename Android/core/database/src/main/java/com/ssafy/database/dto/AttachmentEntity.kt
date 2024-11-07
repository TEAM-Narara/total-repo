package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.model.with.DataStatus

@Entity(tableName = "attachment")
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val cardId: Long = 0L,
    val url: String = "",
    val type: String = "",
    val isCover: Boolean = false,

    val isStatus: DataStatus = DataStatus.STAY
)

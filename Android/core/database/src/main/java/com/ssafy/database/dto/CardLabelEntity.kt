package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.model.with.DataStatus

@Entity(tableName = "card_label")
data class CardLabelEntity(
    @PrimaryKey val id: Long = 0L,
    val labelId: Long = 0L,
    val cardId: Long = 0L,
    val isActivated: Boolean = true,

    val isStatus: DataStatus = DataStatus.STAY
)

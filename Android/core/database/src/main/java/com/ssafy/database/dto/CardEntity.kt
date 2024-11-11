package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.database.dto.bitmask.BitPosition
import com.ssafy.model.with.DataStatus

@Entity(tableName = "card")
data class CardEntity(
    @PrimaryKey(autoGenerate = false)  val id: Long = 0L,
    @BitPosition(0) val listId: Long = 0L,
    @BitPosition(1) val name: String = "",
    @BitPosition(2) val description: String? = null,
    @BitPosition(3) val startAt: Long? = null,
    @BitPosition(4) val endAt: Long? = null,
    @BitPosition(5) val coverType: String? = "COLOR",
    @BitPosition(6) val coverValue: String? = "0xff000000",
    @BitPosition(7) val myOrder: Long = 0L,
    @BitPosition(8) val isArchived: Boolean = false,

    val isStatus: DataStatus = DataStatus.STAY,
    val columnUpdate: Long = 0L,
)

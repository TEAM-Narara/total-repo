package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.model.with.CoverType
import com.ssafy.model.with.DataStatus

@Entity(tableName = "card")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)  val id: Long = 0L,
    val listId: Long = 0L,
    val name: String = "",
    val description: String? = null,
    val startAt: Long? = null,
    val endAt: Long? = null,
    val coverType: CoverType = CoverType.NONE,
    val coverValue: String? = null,
    val myOrder: Long = 0L,
    val isArchived: Boolean = false,

    val isStatus: DataStatus = DataStatus.STAY,
    val columnUpdate: Long = 0L,
)

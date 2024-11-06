package com.ssafy.database.dto.temp

import androidx.room.Embedded
import com.ssafy.database.dto.ListEntity

data class ListInCardThumbnails(
    @Embedded(prefix = "list_") val sbList: ListEntity,
    val cards: List<CardDetail>
)
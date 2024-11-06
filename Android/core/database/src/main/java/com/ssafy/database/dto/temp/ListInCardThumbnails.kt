package com.ssafy.database.dto.temp

import androidx.room.Embedded
import com.ssafy.database.dto.SbList

data class ListInCardThumbnails(
    @Embedded(prefix = "list_") val sbList: SbList,
    val cards: List<CardDetail>
)
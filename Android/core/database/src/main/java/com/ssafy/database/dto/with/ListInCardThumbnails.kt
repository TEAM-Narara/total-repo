package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Card
import com.ssafy.database.dto.SbList

data class ListInCardThumbnails(
    @Embedded(prefix = "list_") val sbList: SbList,
    val cards: List<CardDetail>
)
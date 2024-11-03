package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Card
import com.ssafy.database.dto.SbList

data class ListInCards(
    @Embedded val sbList: SbList,

    @Relation(
        parentColumn = "id",
        entityColumn = "listId",
        entity = Card::class
    )
    val cardThumbnails: List<CardThumbnail>,
)
package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.CardLabel
import com.ssafy.database.dto.Label

data class CardLabelWithLabelInfo(
    @Embedded val cardLabel: CardLabel,

    @Relation(
        parentColumn = "labelId",
        entityColumn = "id"
    )
    val label: Label
)

package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.CardLabel
import com.ssafy.database.dto.Label

data class CardLabelWithLabelInfo(
    @Embedded(prefix = "card_label_")
    val cardLabel: CardLabel,
    @Embedded(prefix = "label_")
    val label: Label
)

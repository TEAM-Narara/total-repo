package com.ssafy.database.dto.with

import androidx.room.Embedded
import com.ssafy.database.dto.CardLabelEntity
import com.ssafy.database.dto.LabelEntity

data class CardLabelWithLabelInfo(
    @Embedded(prefix = "card_label_")
    val cardLabel: CardLabelEntity,
    @Embedded(prefix = "label_")
    val label: LabelEntity
)

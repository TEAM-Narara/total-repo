package com.ssafy.card.label.data

import androidx.compose.ui.graphics.Color
import com.ssafy.model.with.LabelWithCardLabelDTO

data class LabelData(
    val id: Long,
    val color: Color,
    val description: String,
    val isSelected: Boolean,
)

fun LabelWithCardLabelDTO.toLabelData() = with(this) {
    LabelData(
        id = labelId,
        color = Color(labelColor),
        description = labelName,
        isSelected = isActivated
    )
}
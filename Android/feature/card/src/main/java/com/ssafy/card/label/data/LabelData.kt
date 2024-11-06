package com.ssafy.card.label.data

import androidx.compose.ui.graphics.Color

data class LabelData(
    val id: Long,
    val color: Color,
    val description: String,
    val isSelected: Boolean,
)
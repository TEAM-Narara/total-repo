package com.ssafy.board.search.dto

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ssafy.designsystem.values.Gray

sealed interface IconType {
    data class Vector(val image: ImageVector, val backgroundColor: Color = Gray) : IconType
    data class Image(val imageUrl: String) : IconType
    data object None : IconType
}

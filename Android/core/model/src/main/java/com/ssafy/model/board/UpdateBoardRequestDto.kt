package com.ssafy.model.board

import com.ssafy.model.background.Background

data class UpdateBoardRequestDto(
    val name: String,
    val background: Background,
    val visibility: Visibility,
)

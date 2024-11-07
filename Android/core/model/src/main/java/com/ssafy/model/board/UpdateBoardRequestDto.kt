package com.ssafy.model.board

data class UpdateBoardRequestDto(
    val name: String,
    val background: Background,
    val visibility: Visibility,
)
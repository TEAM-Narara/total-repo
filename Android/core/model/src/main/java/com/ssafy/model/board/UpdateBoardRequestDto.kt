package com.ssafy.model.board

import com.ssafy.model.background.Cover

data class UpdateBoardRequestDto(
    val name: String,
    val cover: Cover,
    val visibility: Visibility,
)

package com.ssafy.nullable

import com.ssafy.model.board.Visibility
import com.ssafy.model.with.CoverType

data class UpdateBoardWithNull(
    val name: String?,
    val cover: CoverWithNull?,
    val visibility: Visibility?,
)

data class CoverWithNull(
    val type: CoverType?,
    val value: String?,
)

package com.ssafy.nullable

data class UpdateCardWithNull(
    val name: String?,
    val description: String?,
    val startAt: Long?,
    val endAt: Long?,
    val cover: CoverWithNull?
)

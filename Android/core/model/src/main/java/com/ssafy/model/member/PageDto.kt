package com.ssafy.model.member

data class PageDto(
    val page: Int,
    val size: Int,
    val sort: List<String>
)

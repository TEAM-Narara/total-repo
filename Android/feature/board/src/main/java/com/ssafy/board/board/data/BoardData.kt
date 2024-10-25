package com.ssafy.board.board.data

data class BoardData(
    val id: Long,
    val title: String,
    var listCollection: List<ListData>,
)
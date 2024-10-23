package com.ssafy.board.data

data class BoardData(
    val id: String,
    val title: String,
    var listCollection: List<ListData>,
)
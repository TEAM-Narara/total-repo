package com.ssafy.board.data

data class ListData(
    val id: String,
    val title: String,
    var cardCollection: List<CardData>
)

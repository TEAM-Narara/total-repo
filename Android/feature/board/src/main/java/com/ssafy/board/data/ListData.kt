package com.ssafy.board.data

data class ListData(
    val id: String,
    val title: String,
    val cardCollection: List<CardData>,
    val isWatching: Boolean,
)

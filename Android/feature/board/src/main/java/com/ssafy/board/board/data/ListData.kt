package com.ssafy.board.board.data

data class ListData(
    val id: Long,
    val title: String,
    val cardCollection: List<CardData>,
    val isWatching: Boolean,
)

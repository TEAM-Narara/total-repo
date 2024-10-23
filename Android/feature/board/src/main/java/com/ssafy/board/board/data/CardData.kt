package com.ssafy.board.board.data

data class CardData(
    val id: String,
    val title: String,
)

data class ReorderCardData(
    val id: String,
    val title: String,
    var listId: String? = null
)

fun CardData.toReorderCardData(listId: String? = null) = ReorderCardData(
    id = id,
    title = title,
    listId = listId
)

fun ReorderCardData.toCardData() = CardData(
    id = id,
    title = title
)
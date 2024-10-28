package com.ssafy.board.board.data

data class CardData(
    val id: Long,
    val title: String,
)

data class ReorderCardData(
    val id: Long,
    val title: String,
    var listId: Long? = null
)

fun CardData.toReorderCardData(listId: Long? = null) = ReorderCardData(
    id = id,
    title = title,
    listId = listId
)

fun ReorderCardData.toCardData() = CardData(
    id = id,
    title = title
)
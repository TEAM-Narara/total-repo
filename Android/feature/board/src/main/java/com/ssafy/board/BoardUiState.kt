package com.ssafy.board

data class BoardUiState(
    val id: String,
    val title: String,
    var listCollection: List<ListData>,
)

data class ListData(
    val id: String,
    val title: String,
    var cardCollection: List<CardData>
)

open class CardData(
    open val id: String,
    open val title: String,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is CardData) id == other.id else false
    }

    override fun hashCode(): Int = id.hashCode()
}

data class ReorderCardData(
    override val id: String,
    override val title: String,
    var listId: String? = null
) : CardData(id, title)

fun CardData.toReorderCardData(listId: String? = null) = ReorderCardData(
    id = id,
    title = title,
    listId = listId
)

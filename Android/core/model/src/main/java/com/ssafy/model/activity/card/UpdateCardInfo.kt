package com.ssafy.model.activity.card

data class UpdateCardInfo(
    val listId: Long,
    val listName: String,
    val cardId: Long,
    val cardName: String,
) {
    override fun toString(): String {
        return cardName
    }
}

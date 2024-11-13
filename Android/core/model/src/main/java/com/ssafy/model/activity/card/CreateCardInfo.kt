package com.ssafy.model.activity.card

data class CreateCardInfo(
    val cardId: Long,
    val cardName: String,
    val listId: Long,
    val listName: String,
) {
    override fun toString(): String {
        return cardName
    }
}

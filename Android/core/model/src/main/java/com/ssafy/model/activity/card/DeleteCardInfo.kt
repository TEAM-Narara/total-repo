package com.ssafy.model.activity.card

data class DeleteCardInfo(
    val cardId: Long,
    val listId: Long,
    val listName: String,
    val cardName: String
) {
    override fun toString(): String {
        return cardName
    }
}

package com.ssafy.model.activity.card

data class CardWhereInfo(
    val cardId: Long,
    val cardName: String,
) {
    override fun toString(): String {
        return cardName
    }
}

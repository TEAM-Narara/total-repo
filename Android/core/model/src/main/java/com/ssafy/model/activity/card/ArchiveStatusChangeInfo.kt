package com.ssafy.model.activity.card

data class ArchiveStatusChangeInfo(
    val cardId: Long,
    val cardName: String,
    val isArchived: Boolean,
){
    override fun toString(): String {
        return cardName
    }
}

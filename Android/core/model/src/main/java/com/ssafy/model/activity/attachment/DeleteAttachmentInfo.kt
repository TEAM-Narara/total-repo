package com.ssafy.model.activity.attachment

data class DeleteAttachmentInfo(
    val cardId: Long,
    val cardName: String,
    val url: String,
    val isCover: Boolean
){
    override fun toString(): String {
        return cardName
    }
}

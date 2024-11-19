package com.ssafy.model.activity.reply

data class ReplyInfo(
    val cardId: Long,
    val cardName: String,
    val replyId: Long,
    val replyContent: String,
) {
    override fun toString(): String {
        return replyContent
    }
}

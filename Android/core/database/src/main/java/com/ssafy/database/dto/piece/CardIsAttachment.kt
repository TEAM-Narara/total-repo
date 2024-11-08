package com.ssafy.database.dto.piece

data class CardIsAttachment(
    val cardId: Long,
    val isAttachmentInt: Int
) {
    val isAttachment: Boolean
        get() = isAttachmentInt != 0
}

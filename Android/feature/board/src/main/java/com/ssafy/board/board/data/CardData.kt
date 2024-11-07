package com.ssafy.board.board.data

import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.CardAllInfoDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.model.with.CardMemberDTO
import com.ssafy.model.with.CoverType
import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.ReplyDTO

data class CardData(
    val id: Long,
    val listId: Long,
    val name: String,
    val description: String? = null,
    val startAt: Long? = null,
    val endAt: Long? = null,
    val coverType: CoverType = CoverType.NONE,
    val coverValue: String? = null,
    val myOrder: Long = 0L,
    val isArchived: Boolean = false,

    val isWatching: Boolean,
    val isSynced: Boolean,

    val cardLabels: List<CardLabelDTO>,
    val cardMembers: List<CardMemberDTO>,
    val cardAttachment: List<AttachmentDTO>,
    val cardReplies: List<ReplyDTO>,
)

object CardDataMapper {
    fun fromDto(card: CardAllInfoDTO) = with(card) {
        CardData(
            id = id,
            listId = listId,
            name = name,
            description = description,
            startAt = startAt,
            endAt = endAt,
            coverType = cover?.type ?: CoverType.NONE,
            coverValue = cover?.value,
            myOrder = myOrder,
            isArchived = isArchived,
            isWatching = cardMemberAlarm,
            isSynced = isStatus == DataStatus.STAY,
            cardLabels = cardLabels,
            cardMembers = cardMembers,
            cardAttachment = cardAttachment,
            cardReplies = cardReplies,
        )
    }

    fun fromDto(cards: List<CardAllInfoDTO>): List<CardData> = cards.map(::fromDto)
}

data class ReorderCardData(
    val cardData: CardData,
    var listId: Long? = null
)

fun CardData.toReorderCardData(listId: Long? = null) = ReorderCardData(
    cardData = this,
    listId = listId
)
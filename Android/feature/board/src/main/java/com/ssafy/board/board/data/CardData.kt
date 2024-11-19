package com.ssafy.board.board.data

import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.with.CardLabelWithLabelDTO
import com.ssafy.model.with.CardThumbnail
import com.ssafy.model.with.CoverType
import com.ssafy.model.with.DataStatus

data class CardData(
    val id: Long = 0,
    val listId: Long = 0,
    val name: String = "",
    val description: String? = null,
    val startAt: Long? = null,
    val endAt: Long? = null,
    val coverType: CoverType = CoverType.NONE,
    val coverValue: String? = null,
    val myOrder: Long = 0L,
    val isArchived: Boolean = false,

    val isWatching: Boolean = false,
    val isSynced: Boolean = true,

    val cardLabels: List<CardLabelWithLabelDTO> = emptyList(),
    val cardMembers: List<MemberResponseDTO> = emptyList(),
    val attachment: Boolean = false,
    val replyCount: Int = 0,
)

object CardDataMapper {
    fun fromDto(card: CardThumbnail) = with(card) {
        CardData(
            id = id,
            listId = listId,
            name = name,
            description = description,
            startAt = startAt,
            endAt = endAt,
            coverType = coverType?.let { CoverType.valueOf(it) } ?: CoverType.NONE,
            coverValue = coverValue,
            myOrder = myOrder,
            isArchived = isArchived,
            isWatching = isWatch,
            isSynced = isStatus == DataStatus.STAY,
            cardLabels = cardLabels,
            cardMembers = cardMembers,
            attachment = isAttachment,
            replyCount = replyCount,
        )
    }

    fun fromDto(cards: List<CardThumbnail>): List<CardData> = cards.map(::fromDto)
}

data class ReorderCardData(
    val cardData: CardData,
    var listId: Long? = null
)

fun CardData.toReorderCardData(listId: Long? = null) = ReorderCardData(
    cardData = this,
    listId = listId
)

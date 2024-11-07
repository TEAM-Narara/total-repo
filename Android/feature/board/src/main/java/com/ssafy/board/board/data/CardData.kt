package com.ssafy.board.board.data

import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.with.AttachmentDTO
import com.ssafy.model.with.CardAllInfoDTO
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.model.with.CardLabelWithLabelDTO
import com.ssafy.model.with.CardMemberDTO
import com.ssafy.model.with.CardThumbnail
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

    val cardLabels: List<CardLabelWithLabelDTO>,
    val cardMembers: List<MemberResponseDTO>,
    val attachment: Boolean,
    val replyCount: Int,
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
            coverType = CoverType.NONE, // TODO : Card CoverType 파싱하기
            coverValue = coverValue,
            myOrder = myOrder,
            isArchived = isArchived,
            isWatching = false, // TODO : Card watching 상태 바꾸기
            isSynced = true, // TODO : Card sync 상태 바꾸기
            cardLabels = cardLabels,
            cardMembers = cardMembers,
            attachment = false, // TODO : Card attachment 상태 바꾸기
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
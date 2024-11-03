package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Card
import com.ssafy.database.dto.CardMember
import com.ssafy.database.dto.Reply

data class CardThumbnail(
    @Embedded val card: Card,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId",
        entity = CardMember::class
    )
    val cardRepresentative: CardMemberWithMemberInfo,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId",
    )
    val cardReplies: List<Reply>,
)

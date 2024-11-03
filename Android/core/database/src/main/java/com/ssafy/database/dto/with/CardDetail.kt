package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Attachment
import com.ssafy.database.dto.Card
import com.ssafy.database.dto.CardLabel
import com.ssafy.database.dto.CardMember
import com.ssafy.database.dto.Reply

data class CardDetail(
    @Embedded val card: Card,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId",
        entity = CardLabel::class
    )
    val cardLabels: List<CardLabelWithLabelInfo>,

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
    val cardAttachment: List<Attachment>,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId",
    )
    val cardReplies: List<Reply>,
)

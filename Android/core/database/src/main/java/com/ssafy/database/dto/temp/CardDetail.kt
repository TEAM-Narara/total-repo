package com.ssafy.database.dto.temp

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Attachment
import com.ssafy.database.dto.Card
import com.ssafy.database.dto.CardLabel
import com.ssafy.database.dto.CardMember
import com.ssafy.database.dto.Reply
import com.ssafy.database.dto.with.CardLabelWithLabelInfo
import com.ssafy.database.dto.with.CardMemberWithMemberInfo

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
    val cardRepresentatives: List<CardMemberWithMemberInfo>,

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

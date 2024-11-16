package com.ssafy.database.dto.temp

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.AttachmentEntity
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.CardLabelEntity
import com.ssafy.database.dto.CardMemberEntity
import com.ssafy.database.dto.ReplyEntity
import com.ssafy.database.dto.with.CardLabelWithLabelInfo
import com.ssafy.database.dto.with.CardMemberWithMemberInfo

data class CardDetail(
    @Embedded val card: CardEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId",
        entity = CardLabelEntity::class
    )
    val cardLabels: List<CardLabelWithLabelInfo>,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId",
        entity = CardMemberEntity::class
    )
    val cardRepresentatives: List<CardMemberWithMemberInfo>,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId",
    )
    val cardAttachment: List<AttachmentEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId",
    )
    val cardReplies: List<ReplyEntity>,
)

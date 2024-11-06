package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.AttachmentEntity
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.CardLabelEntity
import com.ssafy.database.dto.CardMemberEntity
import com.ssafy.database.dto.CardMemberAlarmEntity
import com.ssafy.database.dto.ReplyEntity

data class CardAllInfo(
    @Embedded val card: CardEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId",
        entity = CardLabelEntity::class
    )
    val cardLabels: List<CardLabelEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId",
        entity = CardMemberEntity::class
    )
    val cardMembers: List<CardMemberEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "cardId"
    )
    val cardMemberAlarm: CardMemberAlarmEntity?,

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

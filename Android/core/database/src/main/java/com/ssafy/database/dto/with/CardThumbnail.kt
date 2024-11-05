package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Card
import com.ssafy.database.dto.CardLabel
import com.ssafy.database.dto.CardMember
import com.ssafy.database.dto.Member
import com.ssafy.database.dto.Reply

data class CardThumbnail(
    @Embedded(prefix = "card_") val card: Card,

    val replyCount: Int,

    val representativeMembers: List<CardMemberWithMember>,

    val cardLabels: List<CardLabel>
)

data class CardMemberWithMember(
    val id: Long,
    val isRepresentative: Boolean,
    val member: Member
)
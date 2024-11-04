package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.CardMember
import com.ssafy.database.dto.Member

data class CardMemberWithMemberInfo(
    @Embedded val cardMember: CardMember,

    @Relation(
        parentColumn = "memberId",
        entityColumn = "id"
    )
    val member: Member
)

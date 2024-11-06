package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.BoardMember
import com.ssafy.database.dto.CardMember
import com.ssafy.database.dto.Member

data class CardMemberWithMemberInfo(
    @Embedded(prefix = "card_member_")
    val cardMember: CardMember,

    @Embedded(prefix = "member_")
    val member: Member
)

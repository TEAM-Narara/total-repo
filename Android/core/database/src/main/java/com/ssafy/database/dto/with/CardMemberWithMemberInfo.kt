package com.ssafy.database.dto.with

import androidx.room.Embedded
import com.ssafy.database.dto.CardMemberEntity
import com.ssafy.database.dto.MemberEntity

data class CardMemberWithMemberInfo(
    @Embedded(prefix = "card_member_")
    val cardMember: CardMemberEntity,

    @Embedded(prefix = "member_")
    val member: MemberEntity
)

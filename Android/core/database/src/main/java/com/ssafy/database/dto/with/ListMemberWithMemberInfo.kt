package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.CardMember
import com.ssafy.database.dto.Member
import com.ssafy.database.dto.SbListMember

data class ListMemberWithMemberInfo(
    @Embedded val listMember: SbListMember,

    @Relation(
        parentColumn = "memberId",
        entityColumn = "id"
    )
    val member: Member
)

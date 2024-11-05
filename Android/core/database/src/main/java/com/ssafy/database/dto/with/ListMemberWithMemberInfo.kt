package com.ssafy.database.dto.with

import androidx.room.Embedded
import com.ssafy.database.dto.Member
import com.ssafy.database.dto.SbListMember

data class ListMemberWithMemberInfo(
    @Embedded(prefix = "list_member_")
    val listMember: SbListMember,

    @Embedded(prefix = "member_")
    val member: Member
)

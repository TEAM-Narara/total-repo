package com.ssafy.database.dto.with

import androidx.room.Embedded
import com.ssafy.database.dto.MemberEntity
import com.ssafy.database.dto.ListMemberEntity

data class ListMemberWithMemberInfo(
    @Embedded(prefix = "list_member_")
    val listMember: ListMemberEntity,

    @Embedded(prefix = "member_")
    val member: MemberEntity
)

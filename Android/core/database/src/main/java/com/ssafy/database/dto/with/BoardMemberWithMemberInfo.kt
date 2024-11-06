package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.BoardMember
import com.ssafy.database.dto.Member

data class BoardMemberWithMemberInfo(
    @Embedded(prefix = "board_member_")
    val boardMember: BoardMember,

    @Embedded(prefix = "member_")
    val member: Member
)

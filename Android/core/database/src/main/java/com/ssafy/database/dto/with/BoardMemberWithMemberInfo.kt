package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.BoardMember
import com.ssafy.database.dto.Member

data class BoardMemberWithMemberInfo(
    @Embedded val boardMember: BoardMember,

    @Relation(
        parentColumn = "memberId",
        entityColumn = "id"
    )
    val member: Member
)

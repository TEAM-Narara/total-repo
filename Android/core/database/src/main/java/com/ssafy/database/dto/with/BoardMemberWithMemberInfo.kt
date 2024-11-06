package com.ssafy.database.dto.with

import androidx.room.Embedded
import com.ssafy.database.dto.BoardMemberEntity
import com.ssafy.database.dto.MemberEntity

data class BoardMemberWithMemberInfo(
    @Embedded(prefix = "board_member_")
    val boardMember: BoardMemberEntity,

    @Embedded(prefix = "member_")
    val member: MemberEntity
)

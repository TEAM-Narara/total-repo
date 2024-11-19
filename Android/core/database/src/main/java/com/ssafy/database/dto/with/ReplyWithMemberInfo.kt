package com.ssafy.database.dto.with

import androidx.room.Embedded
import com.ssafy.database.dto.MemberEntity
import com.ssafy.database.dto.ReplyEntity

data class ReplyWithMemberInfo(
    @Embedded val reply: ReplyEntity,

    @Embedded(prefix = "member_") val member: MemberEntity
)

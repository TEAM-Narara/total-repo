package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Member
import com.ssafy.database.dto.WorkspaceMember

data class WorkspaceMemberWithMemberInfo(
    @Embedded val workspaceMember: WorkspaceMember,

    @Relation(
        parentColumn = "memberId",
        entityColumn = "id"
    )
    val member: Member
)

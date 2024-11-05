package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Member
import com.ssafy.database.dto.WorkspaceMember

data class WorkspaceMemberWithMemberInfo(
    @Embedded(prefix = "workspace_member_")
    val workspaceMember: WorkspaceMember,

    @Embedded(prefix = "member_")
    val member: Member
)

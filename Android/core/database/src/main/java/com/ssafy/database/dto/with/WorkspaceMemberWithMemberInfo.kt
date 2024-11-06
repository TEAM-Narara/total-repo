package com.ssafy.database.dto.with

import androidx.room.Embedded
import com.ssafy.database.dto.MemberEntity
import com.ssafy.database.dto.WorkspaceMemberEntity

data class WorkspaceMemberWithMemberInfo(
    @Embedded(prefix = "workspace_member_")
    val workspaceMember: WorkspaceMemberEntity,

    @Embedded(prefix = "member_")
    val member: MemberEntity
)

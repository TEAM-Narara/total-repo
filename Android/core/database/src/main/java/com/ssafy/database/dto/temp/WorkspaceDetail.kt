package com.ssafy.database.dto.temp

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Workspace
import com.ssafy.database.dto.WorkspaceMember
import com.ssafy.database.dto.with.WorkspaceMemberWithMemberInfo

data class WorkspaceDetail(
    @Embedded val workspace: Workspace,

    @Relation(
        parentColumn = "id",
        entityColumn = "workspaceId",
        entity = WorkspaceMember::class
    )
    val workspaceMembers: List<WorkspaceMemberWithMemberInfo>,
)

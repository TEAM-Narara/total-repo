package com.ssafy.database.dto.temp

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.database.dto.WorkspaceMemberEntity
import com.ssafy.database.dto.with.WorkspaceMemberWithMemberInfo

data class WorkspaceDetail(
    @Embedded val workspace: WorkspaceEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "workspaceId",
        entity = WorkspaceMemberEntity::class
    )
    val workspaceMembers: List<WorkspaceMemberWithMemberInfo>,
)

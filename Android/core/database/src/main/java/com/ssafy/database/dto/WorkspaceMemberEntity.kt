package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workspace_member")
data class WorkspaceMemberEntity(
    @PrimaryKey val id: Long = 0L,
    val memberId: Long = 0L,
    val workspaceId: Long = 0L,
    val authority: String = "",

    val isStatus: String = "STAY"
)

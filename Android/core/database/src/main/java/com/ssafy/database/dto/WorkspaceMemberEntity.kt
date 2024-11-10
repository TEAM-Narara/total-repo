package com.ssafy.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ssafy.model.member.Authority
import com.ssafy.model.with.DataStatus

@Entity(tableName = "workspace_member", primaryKeys = ["memberId", "workspaceId"])
data class WorkspaceMemberEntity(
    val id: Long = 0L,
    val memberId: Long = 0L,
    val workspaceId: Long = 0L,
    val authority: Authority = Authority.MEMBER,

    val isStatus: DataStatus = DataStatus.STAY
)

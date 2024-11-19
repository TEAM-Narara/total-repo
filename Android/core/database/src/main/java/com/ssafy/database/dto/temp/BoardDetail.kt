package com.ssafy.database.dto.temp

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.BoardMemberEntity
import com.ssafy.database.dto.BoardMemberAlarmEntity
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.database.dto.with.BoardMemberWithMemberInfo

data class BoardDetail(
    @Embedded val board: BoardEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId",
        entity = BoardMemberEntity::class,
    )
    val boardMembers: List<BoardMemberWithMemberInfo>,

    @Relation(
        parentColumn = "workspaceId",
        entityColumn = "id"
    )
    val workspace: WorkspaceEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId"
    )
    val boardMemberAlarm: BoardMemberAlarmEntity?

    // TODO Activity
)

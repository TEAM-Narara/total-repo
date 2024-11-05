package com.ssafy.database.dto.temp

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Board
import com.ssafy.database.dto.BoardMember
import com.ssafy.database.dto.BoardMemberAlarm
import com.ssafy.database.dto.Workspace
import com.ssafy.database.dto.with.BoardMemberWithMemberInfo

data class BoardDetail(
    @Embedded val board: Board,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId",
        entity = BoardMember::class,
    )
    val boardMembers: List<BoardMemberWithMemberInfo>,

    @Relation(
        parentColumn = "workspaceId",
        entityColumn = "id"
    )
    val workspace: Workspace,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId"
    )
    val boardMemberAlarm: BoardMemberAlarm?

    // TODO Activity
)

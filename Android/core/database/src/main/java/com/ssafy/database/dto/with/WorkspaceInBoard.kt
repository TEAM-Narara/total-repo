package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.WorkspaceEntity

data class WorkspaceInBoard(
    @Embedded val workspace: WorkspaceEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "workspaceId",
        entity = BoardEntity::class
    )
    val board: List<BoardInList>,
)

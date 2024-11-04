package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Board
import com.ssafy.database.dto.Workspace

data class WorkspaceInBoard(
    @Embedded val workspace: Workspace,

    @Relation(
        parentColumn = "id",
        entityColumn = "workspaceId",
        entity = Board::class
    )
    val board: List<BoardInList>,
)

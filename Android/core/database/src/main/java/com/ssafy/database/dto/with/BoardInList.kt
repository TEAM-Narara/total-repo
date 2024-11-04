package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.Board
import com.ssafy.database.dto.BoardMember
import com.ssafy.database.dto.BoardMemberAlarm
import com.ssafy.database.dto.Label
import com.ssafy.database.dto.SbList

data class BoardInList(
    @Embedded val board: Board,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId",
        entity = SbList::class
    )
    val lists: List<ListInCards>,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId",
        entity = Label::class
    )
    val labels: List<Label>,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId",
        entity = BoardMember::class
    )
    val boardMembers: List<BoardMember>,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId"
    )
    val boardMemberAlarm: BoardMemberAlarm?
)

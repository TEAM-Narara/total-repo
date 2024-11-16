package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.BoardMemberEntity
import com.ssafy.database.dto.BoardMemberAlarmEntity
import com.ssafy.database.dto.LabelEntity
import com.ssafy.database.dto.ListEntity

data class BoardInList(
    @Embedded val board: BoardEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId",
        entity = ListEntity::class
    )
    val lists: List<ListInCards>,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId",
        entity = LabelEntity::class
    )
    val labels: List<LabelEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId",
        entity = BoardMemberEntity::class
    )
    val boardMembers: List<BoardMemberEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "boardId"
    )
    val boardMemberAlarm: BoardMemberAlarmEntity?
)

package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.SbList
import com.ssafy.database.dto.SbListMember
import com.ssafy.database.dto.SbListMemberAlarm

data class ListInCards(
    @Embedded val list: SbList,

    @Relation(
        parentColumn = "id",
        entityColumn = "listId",
        entity = SbList::class
    )
    val cards: List<CardAllInfo>,

    @Relation(
        parentColumn = "id",
        entityColumn = "listId",
        entity = SbListMember::class
    )
    val listMembers: List<SbListMember>,

    @Relation(
        parentColumn = "id",
        entityColumn = "listId"
    )
    val listMemberAlarm: SbListMemberAlarm?
)

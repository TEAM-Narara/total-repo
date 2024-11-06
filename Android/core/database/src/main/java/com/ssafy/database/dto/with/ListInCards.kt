package com.ssafy.database.dto.with

import androidx.room.Embedded
import androidx.room.Relation
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.ListEntity
import com.ssafy.database.dto.ListMemberEntity
import com.ssafy.database.dto.ListMemberAlarmEntity

data class ListInCards(
    @Embedded val list: ListEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "listId",
        entity = CardEntity::class
    )
    val cards: List<CardAllInfo>,

    @Relation(
        parentColumn = "id",
        entityColumn = "listId",
        entity = ListMemberEntity::class
    )
    val listMembers: List<ListMemberEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "listId"
    )
    val listMemberAlarm: ListMemberAlarmEntity?
)

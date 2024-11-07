package com.ssafy.board.board.data

import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.ListInCard
import com.ssafy.model.with.ListInCardsDTO

data class ListData(
    val id: Long,
    val name: String,
    val myOrder: Long,
    val isArchived: Boolean,
    val cardCollection: List<CardData>,
    val isWatching: Boolean,
    val isSynced: Boolean,
)

object ListDataMapper {
    fun fromDto(list: ListInCard): ListData = with(list) {
        ListData(
            id = id,
            name = name,
            myOrder = myOrder,
            isArchived = isArchived,
            cardCollection = CardDataMapper.fromDto(cards),
            isWatching = false, // TODO : List watching 상태 바꾸기
            isSynced = true, // TODO : LIst Sync 상태 바꾸기
        )
    }

    fun fromDto(lists: List<ListInCard>): List<ListData> = lists.map(::fromDto)
}
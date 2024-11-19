package com.ssafy.board.board.data

import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.ListInCard

data class ListData(
    val id: Long = 0,
    val name: String = "",
    val myOrder: Long = 0,
    val isArchived: Boolean = false,
    val cardCollection: List<CardData> = emptyList(),
    val isWatching: Boolean = false,
    val isSynced: Boolean = true,
)

object ListDataMapper {
    fun fromDto(list: ListInCard): ListData = with(list) {
        ListData(
            id = id,
            name = name,
            myOrder = myOrder,
            isArchived = isArchived,
            cardCollection = CardDataMapper.fromDto(cards),
            isWatching = isWatch,
            isSynced = isStatus == DataStatus.STAY,
        )
    }

    fun fromDto(lists: List<ListInCard>): List<ListData> = lists.map(::fromDto)
}
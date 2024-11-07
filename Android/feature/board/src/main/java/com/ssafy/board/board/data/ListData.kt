package com.ssafy.board.board.data

import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.ListInCardsDTO

data class ListData(
    val id: Long,
    val boardId: Long,
    val name: String,
    val myOrder: Long,
    val isArchived: Boolean,
    val cardCollection: List<CardData>,
    val isWatching: Boolean,
    val isSynced: Boolean,
)

object ListDataMapper {
    fun fromDto(list: ListInCardsDTO): ListData = with(list) {
        ListData(
            id = id,
            boardId = boardId,
            name = name,
            myOrder = myOrder,
            isArchived = isArchived,
            cardCollection = CardDataMapper.fromDto(cards),
            isWatching = listMemberAlarm,
            isSynced = isStatus == DataStatus.STAY
        )
    }

    fun fromDto(lists: List<ListInCardsDTO>): List<ListData> = lists.map(::fromDto)
}
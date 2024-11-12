package com.ssafy.data.repository.order

import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.ListEntity

interface ListMyOrderRepository {
    suspend fun moveListToTop(listId: Long, isConnection: Boolean): ListMoveResult?

    suspend fun moveListToBottom(listId: Long, isConnection: Boolean): ListMoveResult?

    suspend fun moveListBetween(memberId: Long, listId: Long, previousListId: Long, nextListId: Long, isConnection: Boolean): ListMoveResult?
}
package com.ssafy.data.repository.order

interface ListMyOrderRepository {
    suspend fun moveListToTop(listId: Long, isConnection: Boolean): ListMoveResult?

    suspend fun moveListToBottom(listId: Long, isConnection: Boolean): ListMoveResult?

    suspend fun moveListBetween(listId: Long, previousListId: Long, nextListId: Long, isConnection: Boolean): ListMoveResult?
}
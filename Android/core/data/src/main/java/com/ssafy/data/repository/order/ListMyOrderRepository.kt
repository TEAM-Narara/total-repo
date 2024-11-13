package com.ssafy.data.repository.order

interface ListMyOrderRepository {
    suspend fun moveListToTop(listId: Long): ListMoveResult?

    suspend fun moveListToBottom(listId: Long): ListMoveResult?

    suspend fun moveListBetween(listId: Long, previousListId: Long, nextListId: Long): ListMoveResult?
}
package com.ssafy.data.repository.order

interface CardMyOrderRepository {
    suspend fun moveCardToTop(cardId: Long, targetListId: Long, isConnection: Boolean): CardMoveResult?

    suspend fun moveCardToBottom(cardId: Long, targetListId: Long, isConnection: Boolean): CardMoveResult?

    suspend fun moveCardBetween(cardId: Long, previousCardId: Long, nextCardId: Long, isConnection: Boolean): CardMoveResult?
}
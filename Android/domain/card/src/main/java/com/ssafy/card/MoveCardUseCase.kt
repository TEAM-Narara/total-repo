package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.data.repository.order.CardMoveResult
import com.ssafy.data.repository.order.CardMyOrderRepository
import com.ssafy.model.card.CardMoveUpdateRequestDTO
import javax.inject.Inject

class MoveCardUseCase @Inject constructor(
    private val orderRepository: CardMyOrderRepository,
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(
        cardId: Long,
        targetListId: Long,
        prevCardId: Long?,
        nextCardId: Long?,
        isConnected: Boolean,
    ) {
        val cardMoveResult = if (prevCardId == null) {
            orderRepository.moveCardToTop(cardId, targetListId)
        } else if (nextCardId == null) {
            orderRepository.moveCardToBottom(cardId, targetListId)
        } else {
            orderRepository.moveCardBetween(cardId, prevCardId, nextCardId)
        }

        if (cardMoveResult == null) return

        cardRepository.moveCard(
            targetListId,
            cardMoveResult.toCardMoveUpdateRequestDTO(),
            isConnected = isConnected
        )
    }
}

private fun CardMoveResult.toCardMoveUpdateRequestDTO(): List<CardMoveUpdateRequestDTO> =
    when (this) {
        is CardMoveResult.ReorderedCardMove -> response.map {
            CardMoveUpdateRequestDTO(
                it.cardId,
                it.myOrder
            )
        }

        is CardMoveResult.SingleCardMove -> listOf(
            CardMoveUpdateRequestDTO(
                response.cardId,
                response.myOrder
            )
        )
    }

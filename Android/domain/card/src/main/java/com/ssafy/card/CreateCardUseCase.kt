package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.data.repository.order.CardMyOrderRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.card.CardRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CreateCardUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val cardRepository: CardRepository,
    private val cardMyOrderRepository: CardMyOrderRepository,
) {

    suspend operator fun invoke(cardRequestDto: CardRequestDto, isConnected: Boolean): Flow<Long> {
        val memberId = dataStoreRepository.getUser().memberId
        return cardRepository.createCard(memberId, cardRequestDto, isConnected).also {
            if (!isConnected) {
                val id = it.first()
                val cardMoveResult = cardMyOrderRepository.moveCardToBottom(id, cardRequestDto.listId)
                if (cardMoveResult != null) {
                    cardRepository.moveCard(
                        cardRequestDto.listId,
                        cardMoveResult.toCardMoveUpdateRequestDTO(),
                        isConnected = isConnected
                    )
                }
            }
        }
    }
}

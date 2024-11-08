package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.model.card.CardUpdateRequestDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateCardUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {

    suspend operator fun invoke(
        cardUpdateRequestDto: CardUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit> {
        return cardRepository.updateCard(cardUpdateRequestDto, isConnected)
    }

}

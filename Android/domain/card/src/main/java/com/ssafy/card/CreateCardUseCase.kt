package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.model.card.CardRequestDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateCardUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {

    suspend operator fun invoke(cardRequestDto: CardRequestDto, isConnected: Boolean): Flow<Long> {
        return cardRepository.createCard(cardRequestDto, isConnected)
    }

}

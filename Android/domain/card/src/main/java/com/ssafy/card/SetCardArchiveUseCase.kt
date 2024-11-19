package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetCardArchiveUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {

    suspend operator fun invoke(cardId: Long, isConnected: Boolean): Flow<Unit> {
        return cardRepository.setCardArchive(cardId, isConnected)
    }

}

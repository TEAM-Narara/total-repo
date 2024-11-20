package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateAttachmentToCoverUseCase @Inject constructor(
    private val cardRepository: CardRepository,
) {

    suspend operator fun invoke(id: Long, isConnected: Boolean): Flow<Unit> {
        return cardRepository.updateAttachmentToCover(id, isConnected)
    }

}

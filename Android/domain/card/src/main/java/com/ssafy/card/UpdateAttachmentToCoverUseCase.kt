package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import javax.inject.Inject

class UpdateAttachmentToCoverUseCase @Inject constructor(
    private val cardRepository: CardRepository,
) {
    suspend operator fun invoke(id: Long, isConnected: Boolean) {
        cardRepository.updateAttachmentToCover(id, isConnected)
    }
}
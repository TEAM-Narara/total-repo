package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.model.with.AttachmentDTO
import javax.inject.Inject

class CreateAttachmentUseCase @Inject constructor(
    private val cardRepository: CardRepository,
) {
    suspend operator fun invoke(cardId: Long, path: String, isConnected: Boolean) =
        cardRepository.createAttachment(
            attachment = AttachmentDTO(
                cardId = cardId,
                url = path,
                type = "IMAGE"
            ),
            isConnected = isConnected
        )
}
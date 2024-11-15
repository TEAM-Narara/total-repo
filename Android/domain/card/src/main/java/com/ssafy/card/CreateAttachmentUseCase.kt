package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.model.with.AttachmentDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class CreateAttachmentUseCase @Inject constructor(
    private val cardRepository: CardRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(cardId: Long, path: String, isConnected: Boolean): Flow<Long> {
        return cardRepository.createAttachment(
            attachment = AttachmentDTO(
                cardId = cardId,
                url = path,
                type = "IMAGE"
            ),
            isConnected = isConnected
        ).filter {
            val cardEntity = cardRepository.getCard(cardId).firstOrNull()
            return@filter !(cardEntity != null && cardEntity.cover?.value == path)
        }.flatMapLatest { attachmentId ->
            cardRepository.updateAttachmentToCover(attachmentId, isConnected)
            flowOf(attachmentId)
        }
    }
}
package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.model.label.CreateCardLabelRequestDto
import com.ssafy.model.label.UpdateCardLabelActivateRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UpdateCardLabelUseCase @Inject constructor(
    private val cardRepository: CardRepository,
) {
    suspend operator fun invoke(
        labelId: Long,
        cardId: Long,
        isActivated: Boolean,
        isConnected: Boolean
    ): Flow<Unit> {
        val cardLabel = cardRepository.getCardLabel(cardId, labelId)
        return if (cardLabel == null) {
            cardRepository.createCardLabel(
                CreateCardLabelRequestDto(
                    labelId = labelId,
                    cardId = cardId,
                ),
                isConnected
            ).map { Unit }
        } else if (isActivated != cardLabel.isActivated) {
            cardRepository.updateCardLabel(
                UpdateCardLabelActivateRequestDto(
                    labelId = labelId,
                    cardId = cardId,
                ),
                isConnected
            )
        } else {
            flow { Unit }
        }
    }
}
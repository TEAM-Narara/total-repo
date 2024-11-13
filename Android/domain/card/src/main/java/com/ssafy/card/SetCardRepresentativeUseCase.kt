package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import javax.inject.Inject

class SetCardRepresentativeUseCase @Inject constructor(
    private val cardRepository: CardRepository,
) {
    suspend operator fun invoke(cardId: Long, memberId: Long, isRepresentative: Boolean) {
        cardRepository.setCardPresenter(cardId, memberId)
    }
}
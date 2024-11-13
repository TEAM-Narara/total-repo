package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.datastore.DataStoreRepository
import javax.inject.Inject

class SetCardAlertStatusUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val cardRepository: CardRepository,
) {
    suspend operator fun invoke(cardId: Long, isAlert: Boolean) {
        val memberId = dataStoreRepository.getUser().memberId
        cardRepository.setCardAlertStatus(cardId, memberId)
    }
}
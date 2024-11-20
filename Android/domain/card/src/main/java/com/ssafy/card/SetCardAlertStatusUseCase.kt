package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.datastore.DataStoreRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SetCardAlertStatusUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val cardRepository: CardRepository,
) {
    suspend operator fun invoke(cardId: Long, isAlert: Boolean, isConnected: Boolean) = flow {
        val memberId = dataStoreRepository.getUser().memberId

        if (!isConnected) {
            throw RuntimeException("알림 설정은 온라인 상태에서만 가능합니다.")
        }

        emit(cardRepository.setCardAlertStatus(cardId, memberId))
    }
}
package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SetCardRepresentativeUseCase @Inject constructor(
    private val cardRepository: CardRepository,
) {

    suspend operator fun invoke(
        cardId: Long,
        memberId: Long,
        isRepresentative: Boolean,
        isConnected: Boolean
    ) = flow {
        if (!isConnected) {
            throw RuntimeException("담당자 변경은 온라인 상태에서만 가능합니다.")
        }

        emit(cardRepository.setCardPresenter(cardId, memberId))
    }

}

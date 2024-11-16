package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.with.BoardInMyRepresentativeCard
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyCardUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val cardRepository: CardRepository
) {

    suspend operator fun invoke(): Flow<List<BoardInMyRepresentativeCard>> {
        val memberId = dataStoreRepository.getUser().memberId
        return cardRepository.getLocalScreenMyRepresentativeCard(memberId)
    }

}

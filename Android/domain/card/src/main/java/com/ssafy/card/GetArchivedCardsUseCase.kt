package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.model.card.CardResponseDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArchivedCardsUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {

    suspend operator fun invoke(boardId: Long): Flow<List<CardResponseDto>> {
        return cardRepository.getArchivedCards(boardId)
    }

}

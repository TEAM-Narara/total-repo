package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.model.board.MemberResponseDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCardMemberUseCase @Inject constructor(
    private val cardRepository: CardRepository,
) {
    suspend operator fun invoke(cardId: Long): Flow<List<MemberResponseDTO>> =
        cardRepository.getCardMembers(cardId)
}
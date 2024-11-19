package com.ssafy.card

import com.ssafy.data.repository.card.CardRepository
import com.ssafy.model.with.MemberWithRepresentativeDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCardMemberUseCase @Inject constructor(
    private val cardRepository: CardRepository,
) {
    suspend operator fun invoke(
        workspaceId: Long,
        boardId: Long,
        cardId: Long
    ): Flow<List<MemberWithRepresentativeDTO>> {
        return cardRepository.getMembersWithRepresentativeFlag(workspaceId, boardId, cardId)
    }
}
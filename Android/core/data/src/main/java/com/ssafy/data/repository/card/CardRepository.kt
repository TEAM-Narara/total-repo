package com.ssafy.data.repository.card

import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    suspend fun createCard(cardRequestDto: CardRequestDto, isConnected: Boolean): Flow<Unit>

    suspend fun deleteCard(cardId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun updateCard(
        cardId: Long,
        cardUpdateRequestDto: CardUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun setCardArchive(cardId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getArchivedCards(boardId: Long): Flow<List<CardResponseDto>>

}

package com.ssafy.network.source.card

import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import kotlinx.coroutines.flow.Flow

interface CardDataSource {

    suspend fun createCard(cardRequestDto: CardRequestDto): Flow<Unit>

    suspend fun deleteCard(cardId: Long): Flow<Unit>

    suspend fun updateCard(
        cardId: Long,
        cardUpdateRequestDto: CardUpdateRequestDto
    ): Flow<Unit>

    suspend fun setCardArchive(cardId: Long): Flow<Unit>

    suspend fun getArchivedCards(boardId: Long): Flow<List<CardResponseDto>>

}

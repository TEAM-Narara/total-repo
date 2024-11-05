package com.ssafy.network.source.card

import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.network.api.CardAPI
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CardDataSourceImpl @Inject constructor(private val cardAPI: CardAPI) : CardDataSource {

    override suspend fun createCard(cardRequestDto: CardRequestDto): Flow<Unit> =
        cardAPI.createCard(cardRequestDto).toFlow()

    override suspend fun deleteCard(cardId: Long): Flow<Unit> =
        cardAPI.deleteCard(cardId).toFlow()

    override suspend fun updateCard(
        cardId: Long,
        cardUpdateRequestDto: CardUpdateRequestDto
    ): Flow<Unit> = cardAPI.updateCard(cardId, cardUpdateRequestDto).toFlow()

    override suspend fun setCardArchive(cardId: Long): Flow<Unit> =
        cardAPI.setCardArchive(cardId).toFlow()

    override suspend fun getArchivedCards(boardId: Long): Flow<List<CardResponseDto>> =
        cardAPI.getArchivedCards(boardId).toFlow()

}

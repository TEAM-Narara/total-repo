package com.ssafy.network.source.card

import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.network.api.CardAPI
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import javax.inject.Inject

class CardDataSourceImpl @Inject constructor(private val cardAPI: CardAPI) : CardDataSource {

    override suspend fun createCard(cardRequestDto: CardRequestDto): Response<ApiResponse<CardResponseDto>> =
        cardAPI.createCard(cardRequestDto)

    override suspend fun deleteCard(cardId: Long): Response<ApiResponse<Unit>> =
        cardAPI.deleteCard(cardId)

    override suspend fun updateCard(
        cardId: Long,
        cardUpdateRequestDto: CardUpdateRequestDto
    ): Response<ApiResponse<CardResponseDto>> = cardAPI.updateCard(cardId, cardUpdateRequestDto)

    override suspend fun setCardArchive(cardId: Long): Response<ApiResponse<Unit>> =
        cardAPI.setCardArchive(cardId)

    override suspend fun getArchivedCards(boardId: Long): Response<ApiResponse<List<CardResponseDto>>> =
        cardAPI.getArchivedCards(boardId)

}

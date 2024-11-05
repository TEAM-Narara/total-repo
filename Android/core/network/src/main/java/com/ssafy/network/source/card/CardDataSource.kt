package com.ssafy.network.source.card

import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.network.source.ApiResponse
import retrofit2.Response

interface CardDataSource {

    suspend fun createCard(cardRequestDto: CardRequestDto): Response<ApiResponse<Unit>>

    suspend fun deleteCard(cardId: Long): Response<ApiResponse<Unit>>

    suspend fun updateCard(
        cardId: Long,
        cardUpdateRequestDto: CardUpdateRequestDto
    ): Response<ApiResponse<Unit>>

    suspend fun setCardArchive(cardId: Long): Response<ApiResponse<Unit>>

    suspend fun getArchivedCards(boardId: Long): Response<ApiResponse<List<CardResponseDto>>>

}

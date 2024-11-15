package com.ssafy.network.api

import com.ssafy.model.label.CreateCardLabelRequestDto
import com.ssafy.model.label.UpdateCardLabelActivateRequestDto
import com.ssafy.model.with.CardLabelDTO
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CardLabelAPI {
    @POST("api/v1/card-label/create")
    suspend fun createCardLabel(
        @Body createCardLabelRequestDto: CreateCardLabelRequestDto
    ): Response<ApiResponse<CardLabelDTO>>

    @GET("api/v1/card-label/{cardId}")
    suspend fun getCardLabels(
        @Path("cardId") cardId: Long,
    ): Response<ApiResponse<List<CardLabelDTO>>>

    @PATCH("api/v1/card-label/activate")
    suspend fun updateLabelActivate(
        @Body updateCardLabelActivateRequestDto: UpdateCardLabelActivateRequestDto
    ): Response<ApiResponse<CardLabelDTO>>
}
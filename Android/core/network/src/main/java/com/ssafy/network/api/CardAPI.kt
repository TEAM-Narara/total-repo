package com.ssafy.network.api

import com.ssafy.model.attachment.AttachmentResponseDto
import com.ssafy.model.card.CardMoveUpdateRequestDTO
import com.ssafy.model.card.CardRequestDto
import com.ssafy.model.card.CardResponseDto
import com.ssafy.model.card.CardUpdateRequestDto
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CardAPI {

    @POST("api/v1/card")
    suspend fun createCard(@Body cardRequestDto: CardRequestDto): Response<ApiResponse<Unit>>

    @DELETE("api/v1/card/{cardId}")
    suspend fun deleteCard(@Path("cardId") cardId: Long): Response<ApiResponse<Unit>>

    @PATCH("api/v1/card/{cardId}")
    suspend fun updateCard(
        @Path("cardId") cardId: Long,
        @Body cardUpdateRequestDto: CardUpdateRequestDto
    ): Response<ApiResponse<Unit>>

    @PATCH("api/v1/card/{listId}/move")
    suspend fun moveCard(@Path("listId") listId: Long,
                         @Body cardMoveUpdateRequestDTO: List<CardMoveUpdateRequestDTO>): Response<ApiResponse<Unit>>

    @PATCH("api/v1/card/{cardId}/archive")
    suspend fun setCardArchive(@Path("cardId") cardId: Long): Response<ApiResponse<Unit>>

    @GET("api/v1/cared/archived/{boardId}")
    suspend fun getArchivedCards(@Path("boardId") boardId: Long): Response<ApiResponse<List<CardResponseDto>>>

    @POST("api/v1/attachment")
    suspend fun createAttachment(
        @Query("cardId") cardId: Long,
        @Query("url") url: String,
    ): Response<ApiResponse<AttachmentResponseDto>>

    @DELETE("api/v1/attachment/{attachmentId}")
    suspend fun deleteAttachment(@Path("attachmentId") attachmentId: Long): Response<ApiResponse<Unit>>

    @PATCH("api/v1/attachment/{attachmentId}/cover")
    suspend fun updateAttachmentToCover(@Path("attachmentId") attachmentId: Long): Response<ApiResponse<Unit>>
}

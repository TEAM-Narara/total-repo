package com.ssafy.network.api

import com.ssafy.model.label.CreateLabelRequestDto
import com.ssafy.model.label.LabelDTO
import com.ssafy.model.label.UpdateLabelRequestDto
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LabelAPI {
    @Headers("Accept: application/json")
    @POST("api/v1/label")
    suspend fun createLabel(
        @Query("boardId") boardId: Long,
        @Body createLabelRequestDto: CreateLabelRequestDto
    ): Response<ApiResponse<LabelDTO>>

    @Headers("Accept: application/json")
    @GET("api/v1/label/{labelId}")
    suspend fun getLabel(
        @Path("labelId") labelId: Long,
    ): Response<ApiResponse<LabelDTO>>

    @Headers("Accept: application/json")
    @DELETE("api/v1/label/{labelId}")
    suspend fun deleteLabel(
        @Path("labelId") labelId: Long,
    ): Response<ApiResponse<Unit>>

    @Headers("Accept: application/json")
    @PATCH("api/v1/label/{labelId}")
    suspend fun updateLabel(
        @Path("labelId") labelId: Long,
        @Body updateLabelRequestDto: UpdateLabelRequestDto
    ): Response<ApiResponse<LabelDTO>>
}
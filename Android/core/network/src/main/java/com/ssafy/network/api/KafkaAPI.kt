package com.ssafy.network.api

import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KafkaAPI {
    @GET("api/v1/kafka/messages")
    suspend fun sync(
        @Query("partition") partition: Long,
        @Query("offset") offset: Long,
        @Query("entityType") entityType: String,
        @Query("primaryId") primaryId: Long
    ): Response<ApiResponse<Unit>>
}

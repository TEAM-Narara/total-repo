package com.ssafy.network.api

import com.ssafy.model.fcm.FcmDTO
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FcmAPI {

    @POST("api/v1/fcm-token")
    suspend fun registerFcmToken(
        @Query("memberId") memberId: Long,
        @Query("registrationToken") fcmToken: String
    ): Response<ApiResponse<FcmDTO>>

    @PATCH("api/v1/fcm-token")
    suspend fun updateFcmToken(
        @Query("memberId") memberId: Long,
        @Query("registrationToken") fcmToken: String
    ): Response<ApiResponse<FcmDTO>>

    @DELETE("api/v1/fcm-token/{memberId}")
    suspend fun deleteFcmToken(
        @Path("memberId") memberId: Long
    ): Response<ApiResponse<Unit>>

}

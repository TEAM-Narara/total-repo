package com.ssafy.network.api

import com.ssafy.network.auth.AuthInterceptor.Companion.REFRESH_TOKEN
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthAPI {

    @POST("api/v1/members/reissue")
    suspend fun reissue(@Header(REFRESH_TOKEN) refreshToken: String): Response<ApiResponse<Unit>>

}

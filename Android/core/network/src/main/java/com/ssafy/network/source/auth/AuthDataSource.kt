package com.ssafy.network.source.auth

import com.ssafy.network.source.ApiResponse
import retrofit2.Response


interface AuthDataSource {
    suspend fun reissue(refreshToken: String): Response<ApiResponse<Unit>>
}

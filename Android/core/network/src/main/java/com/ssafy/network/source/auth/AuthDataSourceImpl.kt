package com.ssafy.network.source.auth

import com.ssafy.network.api.AuthAPI
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(private val authAPI: AuthAPI) : AuthDataSource {

    override suspend fun reissue(refreshToken: String): Response<ApiResponse<Unit>> =
        authAPI.reissue(refreshToken)

}

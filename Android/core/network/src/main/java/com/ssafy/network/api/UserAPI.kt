package com.ssafy.network.api

import com.ssafy.model.user.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("api/v1/user/login/github")
    suspend fun loginWithGitHub(@Body token: String): Response<User>

    @POST("api/v1/user/login/naver")
    suspend fun loginWithNaver(@Body token: String): Response<User>

    @POST("api/v1/user/login")
    suspend fun login(@Body user: User): Response<User>

}

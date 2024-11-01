package com.ssafy.network.api

import com.ssafy.model.user.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface UserAPI {

    @POST("api/v1/members/aouth2/login/github")
    suspend fun loginWithGitHub(@Query("accessToken") token: String): Response<User>

    @POST("api/v1/members/aouth2/login/naver")
    suspend fun loginWithNaver(@Query("accessToken") token: String): Response<User>

    @POST("api/v1/user/login")
    suspend fun login(@Body user: User): Response<User>

}

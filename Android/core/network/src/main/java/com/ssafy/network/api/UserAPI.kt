package com.ssafy.network.api

import com.ssafy.model.user.User
import com.ssafy.model.user.signup.RegisterDTO
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserAPI {

    @POST("api/v1/members/oauth2/login/github")
    suspend fun loginWithGitHub(@Query("accessToken") token: String): Response<ApiResponse<User>>

    @POST("api/v1/members/oauth2/login/naver")
    suspend fun loginWithNaver(@Query("accessToken") token: String): Response<ApiResponse<User>>

    @POST("api/v1/members/login")
    suspend fun login(@Body body: Map<String, String>): Response<ApiResponse<User>>

    @POST("api/v1/members/email-code")
    suspend fun sendEmailCode(@Query("email") email: String): Response<ApiResponse<Unit>>

    @POST("api/v1/members/email-code/verify")
    suspend fun verifyEmailCode(
        @Query("email") email: String,
        @Query("code") code: String
    ): Response<ApiResponse<Unit>>

    @POST("api/v1/members/register")
    suspend fun register(@Body registerDTO: RegisterDTO): Response<ApiResponse<User>>


    @GET("api/v1/members/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

}

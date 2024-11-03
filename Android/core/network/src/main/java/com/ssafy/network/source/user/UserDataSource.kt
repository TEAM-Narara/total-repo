package com.ssafy.network.source.user

import com.ssafy.model.user.User
import com.ssafy.model.user.signup.RegisterDTO
import com.ssafy.network.source.ApiResponse
import retrofit2.Response

interface UserDataSource {

    suspend fun loginWithGitHub(token: String): Response<ApiResponse<User>>

    suspend fun loginWithNaver(token: String): Response<ApiResponse<User>>

    suspend fun login(email: String, password: String): Response<ApiResponse<User>>

    suspend fun sendEmailCode(email: String): Response<ApiResponse<Unit>>

    suspend fun verifyEmailCode(email: String, code: String): Response<ApiResponse<Unit>>

    suspend fun register(registerDTO: RegisterDTO): Response<ApiResponse<User>>

}

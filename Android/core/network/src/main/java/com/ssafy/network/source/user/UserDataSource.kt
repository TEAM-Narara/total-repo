package com.ssafy.network.source.user

import com.ssafy.model.user.User
import com.ssafy.model.user.signup.RegisterDTO
import retrofit2.Response

interface UserDataSource {

    suspend fun loginWithGitHub(token: String): Response<User>

    suspend fun loginWithNaver(token: String): Response<User>

    suspend fun login(email: String, password: String): Response<User>

    suspend fun sendEmailCode(email: String): Response<Unit>

    suspend fun verifyEmailCode(email: String, code: String): Response<Unit>

    suspend fun register(registerDTO: RegisterDTO): Response<User>

}

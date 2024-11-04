package com.ssafy.network.source.user

import com.ssafy.model.user.User
import com.ssafy.model.user.signup.RegisterDTO
import com.ssafy.network.api.UserAPI
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(private val userAPI: UserAPI) : UserDataSource {

    override suspend fun loginWithGitHub(token: String): Response<ApiResponse<User>> =
        userAPI.loginWithGitHub(token)

    override suspend fun loginWithNaver(token: String): Response<ApiResponse<User>> =
        userAPI.loginWithNaver(token)

    override suspend fun login(email: String, password: String): Response<ApiResponse<User>> {
        val map = mapOf("email" to email, "password" to password)
        return userAPI.login(map)
    }

    override suspend fun sendEmailCode(email: String): Response<ApiResponse<Unit>> =
        userAPI.sendEmailCode(email)

    override suspend fun verifyEmailCode(email: String, code: String): Response<ApiResponse<Unit>> =
        userAPI.verifyEmailCode(email, code)

    override suspend fun register(registerDTO: RegisterDTO): Response<ApiResponse<User>> =
        userAPI.register(registerDTO)

    override suspend fun logout(): Response<ApiResponse<Unit>> =
        userAPI.logout()

    override suspend fun withdrawal(): Response<ApiResponse<Unit>> =
        userAPI.withdrawal()

}

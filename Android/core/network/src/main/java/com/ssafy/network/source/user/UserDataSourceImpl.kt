package com.ssafy.network.source.user

import com.ssafy.model.user.User
import com.ssafy.model.user.signup.RegisterDTO
import com.ssafy.network.api.UserAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(private val userAPI: UserAPI) : UserDataSource {

    override suspend fun loginWithGitHub(token: String): Flow<User> =
        safeApiCall { userAPI.loginWithGitHub(token) }.toFlow()

    override suspend fun loginWithNaver(token: String): Flow<User> =
        safeApiCall { userAPI.loginWithNaver(token) }.toFlow()

    override suspend fun login(email: String, password: String): Flow<User> {
        val map = mapOf("email" to email, "password" to password)
        return safeApiCall { userAPI.login(map) }.toFlow()
    }

    override suspend fun sendEmailCode(email: String): Flow<Unit> =
        safeApiCall { userAPI.sendEmailCode(email) }.toFlow()

    override suspend fun verifyEmailCode(email: String, code: String): Flow<Unit> =
        safeApiCall { userAPI.verifyEmailCode(email, code) }.toFlow()

    override suspend fun register(registerDTO: RegisterDTO): Flow<User> =
        safeApiCall { userAPI.register(registerDTO) }.toFlow()

    override suspend fun logout(): Flow<Unit> =
        safeApiCall { userAPI.logout() }.toFlow()

    override suspend fun withdrawal(): Flow<Unit> =
        safeApiCall { userAPI.withdrawal() }.toFlow()

}

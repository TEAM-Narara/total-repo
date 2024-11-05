package com.ssafy.network.source.user

import com.ssafy.model.user.User
import com.ssafy.model.user.signup.RegisterDTO
import com.ssafy.network.api.UserAPI
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(private val userAPI: UserAPI) : UserDataSource {

    override suspend fun loginWithGitHub(token: String): Flow<User> =
        userAPI.loginWithGitHub(token).toFlow()

    override suspend fun loginWithNaver(token: String): Flow<User> =
        userAPI.loginWithNaver(token).toFlow()

    override suspend fun login(email: String, password: String): Flow<User> {
        val map = mapOf("email" to email, "password" to password)
        return userAPI.login(map).toFlow()
    }

    override suspend fun sendEmailCode(email: String): Flow<Unit> =
        userAPI.sendEmailCode(email).toFlow()

    override suspend fun verifyEmailCode(email: String, code: String): Flow<Unit> =
        userAPI.verifyEmailCode(email, code).toFlow()

    override suspend fun register(registerDTO: RegisterDTO): Flow<User> =
        userAPI.register(registerDTO).toFlow()

    override suspend fun logout(): Flow<Unit> =
        userAPI.logout().toFlow()

    override suspend fun withdrawal(): Flow<Unit> =
        userAPI.withdrawal().toFlow()

}

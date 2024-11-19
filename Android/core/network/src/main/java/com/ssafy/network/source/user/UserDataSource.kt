package com.ssafy.network.source.user

import com.ssafy.model.user.User
import com.ssafy.model.user.signup.RegisterDTO
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    suspend fun loginWithGitHub(token: String): Flow<User>

    suspend fun loginWithNaver(token: String): Flow<User>

    suspend fun login(email: String, password: String): Flow<User>

    suspend fun sendEmailCode(email: String): Flow<Unit>

    suspend fun verifyEmailCode(email: String, code: String): Flow<Unit>

    suspend fun register(registerDTO: RegisterDTO): Flow<User>

    suspend fun logout(): Flow<Unit>

    suspend fun withdrawal(): Flow<Unit>

}

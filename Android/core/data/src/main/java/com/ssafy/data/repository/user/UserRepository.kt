package com.ssafy.data.repository.user

import com.ssafy.model.user.User
import com.ssafy.model.user.signup.RegisterDTO
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun loginWithGitHub(token: String): Flow<User>

    suspend fun loginWithNaver(token: String): Flow<User>

    suspend fun login(email: String, password: String): Flow<User>

    suspend fun sendEmailCode(email: String): Flow<Unit>

    suspend fun verifyEmailCode(email: String, code: String): Flow<Unit>

    suspend fun register(registerDTO: RegisterDTO): Flow<User>

}

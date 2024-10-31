package com.ssafy.data.repository.user

import com.ssafy.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun loginWithGitHub(token: String): Flow<User>

    suspend fun loginWithNaver(token: String): Flow<User>

    suspend fun login(user: User): Flow<User>
}

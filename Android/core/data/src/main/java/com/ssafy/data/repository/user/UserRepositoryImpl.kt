package com.ssafy.data.repository.user

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toFlow
import com.ssafy.network.source.user.UserDataSource
import com.ssafy.model.user.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

    override suspend fun loginWithGitHub(token: String): Flow<User> {
        val response = withContext(ioDispatcher) { userDataSource.loginWithGitHub(token) }
        return response.toFlow()
    }

    override suspend fun loginWithNaver(token: String): Flow<User> {
        val response = withContext(ioDispatcher) { userDataSource.loginWithNaver(token) }
        return response.toFlow()
    }

    override suspend fun login(user: User): Flow<User> {
        val response = withContext(ioDispatcher) { userDataSource.login(user) }
        return response.toFlow()
    }

}

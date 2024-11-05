package com.ssafy.data.repository.user

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toFlow
import com.ssafy.model.user.User
import com.ssafy.model.user.signup.RegisterDTO
import com.ssafy.network.source.user.UserDataSource
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

    override suspend fun login(email: String, password: String): Flow<User> {
        val response = withContext(ioDispatcher) { userDataSource.login(email, password) }
        return response.toFlow()
    }


    override suspend fun sendEmailCode(email: String): Flow<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.sendEmailCode(email) }
        return response.toFlow()
    }

    override suspend fun verifyEmailCode(email: String, code: String): Flow<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.verifyEmailCode(email, code) }
        return response.toFlow()
    }

    override suspend fun register(registerDTO: RegisterDTO): Flow<User> {
        val response = withContext(ioDispatcher) { userDataSource.register(registerDTO) }
        return response.toFlow()
    }

    override suspend fun logout(): Flow<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.logout() }
        return response.toFlow()
    }

    override suspend fun withdrawal(): Flow<Unit> {
        val response = withContext(ioDispatcher) { userDataSource.withdrawal() }
        return response.toFlow()
    }

}

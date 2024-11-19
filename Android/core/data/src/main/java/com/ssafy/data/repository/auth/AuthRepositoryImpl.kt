package com.ssafy.data.repository.auth

import com.ssafy.network.source.auth.AuthDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun reissue(refreshToken: String): String? {
        val response = authDataSource.reissue(refreshToken).raw()
        return response.header(TOKEN)
    }

    companion object {
        const val TOKEN = "Authorization"
    }
}
package com.ssafy.data.repository.auth

interface AuthRepository {

    suspend fun reissue(refreshToken: String): String?

}

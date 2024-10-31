package com.ssafy.network.source.user

import com.ssafy.model.user.User
import retrofit2.Response

interface UserDataSource {

    suspend fun loginWithGitHub(token: String): Response<User>

    suspend fun loginWithNaver(token: String): Response<User>

    suspend fun login(user: User): Response<User>

}

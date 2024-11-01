package com.ssafy.network.source.user

import com.ssafy.model.user.User
import com.ssafy.network.api.UserAPI
import retrofit2.Response
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(private val userAPI: UserAPI) : UserDataSource {

    override suspend fun loginWithGitHub(token: String): Response<User> =
        userAPI.loginWithGitHub(token)

    override suspend fun loginWithNaver(token: String): Response<User> =
        userAPI.loginWithNaver(token)

    override suspend fun login(email: String, password: String): Response<User> {
        val map = mapOf("email" to email, "password" to password)
        return userAPI.login(map)
    }

}

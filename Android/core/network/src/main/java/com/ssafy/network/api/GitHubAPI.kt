package com.ssafy.network.api

import com.ssafy.model.user.github.GitHubDTO
import com.ssafy.model.user.github.GitHubResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GitHubAPI {

    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    suspend fun getAccessToken(@Body gitHubDTO: GitHubDTO): Response<GitHubResponse>
}

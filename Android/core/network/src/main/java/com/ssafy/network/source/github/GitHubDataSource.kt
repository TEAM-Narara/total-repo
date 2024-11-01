package com.ssafy.network.source.github

import com.ssafy.model.user.github.GitHubDTO
import com.ssafy.model.user.github.GitHubResponse
import retrofit2.Response

interface GitHubDataSource {
    suspend fun getAccessToken(gitHubDTO: GitHubDTO): Response<GitHubResponse>
}

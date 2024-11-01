package com.ssafy.network.source.github

import com.ssafy.model.user.github.GitHubDTO
import com.ssafy.model.user.github.GitHubResponse
import com.ssafy.network.api.GitHubAPI
import retrofit2.Response
import javax.inject.Inject

class GitHubDataSourceImpl @Inject constructor(
    private val gitHubAPI: GitHubAPI
) : GitHubDataSource {

    override suspend fun getAccessToken(gitHubDTO: GitHubDTO): Response<GitHubResponse> =
        gitHubAPI.getAccessToken(gitHubDTO)

}

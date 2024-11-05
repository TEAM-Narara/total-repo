package com.ssafy.network.source.github

import com.ssafy.model.user.OAuth
import com.ssafy.model.user.github.GitHubDTO
import com.ssafy.model.user.github.GitHubResponse
import com.ssafy.network.api.GitHubAPI
import com.ssafy.network.source.ERROR
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class GitHubDataSourceImpl @Inject constructor(
    private val gitHubAPI: GitHubAPI
) : GitHubDataSource {

    override suspend fun getAccessToken(gitHubDTO: GitHubDTO): Flow<OAuth> =
        gitHubAPI.getAccessToken(gitHubDTO).toFlow()
}

private fun Response<GitHubResponse>.toFlow(): Flow<OAuth> {
    return flow {
        body()?.let {
            if (isSuccessful) emit(OAuth.GitHub(it.accessToken))
            else throw RuntimeException(ERROR)
        }
    }
}
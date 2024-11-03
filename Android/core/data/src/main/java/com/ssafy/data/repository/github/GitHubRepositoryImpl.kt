package com.ssafy.data.repository.github

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.ERROR
import com.ssafy.model.user.OAuth
import com.ssafy.model.user.github.GitHubDTO
import com.ssafy.model.user.github.GitHubResponse
import com.ssafy.network.source.github.GitHubDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRepositoryImpl @Inject constructor(
    private val gitHubDataSource: GitHubDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GitHubRepository {

    override suspend fun getAccessToken(gitHubDTO: GitHubDTO): Flow<OAuth> {
        val response = withContext(ioDispatcher) { gitHubDataSource.getAccessToken(gitHubDTO) }
        return response.toFlow()
    }

    private fun Response<GitHubResponse>.toFlow(): Flow<OAuth> {
        return flow {
            body()?.let {
                if (isSuccessful) emit(OAuth.GitHub(it.accessToken))
                else throw RuntimeException(ERROR)
            }
        }
    }

}

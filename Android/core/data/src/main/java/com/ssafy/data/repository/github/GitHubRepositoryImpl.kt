package com.ssafy.data.repository.github

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toFlow
import com.ssafy.network.source.github.GitHubDataSource
import com.ssafy.model.user.OAuth
import com.ssafy.model.user.github.GitHubDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRepositoryImpl @Inject constructor(
    private val gitHubDataSource: GitHubDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GitHubRepository {

    override suspend fun getAccessToken(gitHubDTO: GitHubDTO): Flow<OAuth> {
        val response = withContext(ioDispatcher) { gitHubDataSource.getAccessToken(gitHubDTO) }
        return response.toFlow().map { OAuth.GitHub(it.accessToken) }
    }

}

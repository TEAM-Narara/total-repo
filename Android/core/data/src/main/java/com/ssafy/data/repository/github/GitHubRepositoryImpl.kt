package com.ssafy.data.repository.github

import com.ssafy.data.di.IoDispatcher
import com.ssafy.model.user.OAuth
import com.ssafy.model.user.github.GitHubDTO
import com.ssafy.network.source.github.GitHubDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRepositoryImpl @Inject constructor(
    private val gitHubDataSource: GitHubDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GitHubRepository {

    override suspend fun getAccessToken(gitHubDTO: GitHubDTO): Flow<OAuth> =
        withContext(ioDispatcher) { gitHubDataSource.getAccessToken(gitHubDTO) }
}

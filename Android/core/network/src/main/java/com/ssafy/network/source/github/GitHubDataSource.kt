package com.ssafy.network.source.github

import com.ssafy.model.user.OAuth
import com.ssafy.model.user.github.GitHubDTO
import kotlinx.coroutines.flow.Flow

interface GitHubDataSource {
    suspend fun getAccessToken(gitHubDTO: GitHubDTO): Flow<OAuth>
}

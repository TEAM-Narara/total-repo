package com.ssafy.data.repository.github

import com.ssafy.model.user.OAuth
import com.ssafy.model.user.github.GitHubDTO
import kotlinx.coroutines.flow.Flow

interface GitHubRepository {

    suspend fun getAccessToken(gitHubDTO: GitHubDTO): Flow<OAuth>

}

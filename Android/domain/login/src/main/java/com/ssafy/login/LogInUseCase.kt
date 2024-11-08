package com.ssafy.login

import com.ssafy.data.repository.github.GitHubRepository
import com.ssafy.data.repository.user.UserRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.user.OAuth
import com.ssafy.model.user.github.GitHubDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LogInUseCase @Inject constructor(
    private val gitHubRepository: GitHubRepository,
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(gitHubDTO: GitHubDTO): Flow<Boolean> {
        return gitHubRepository.getAccessToken(gitHubDTO)
            .flatMapLatest { token -> invoke(token) }
    }

    suspend operator fun invoke(oAuth: OAuth): Flow<Boolean> {
        return when (oAuth) {
            is OAuth.GitHub -> userRepository.loginWithGitHub(oAuth.token)
            is OAuth.Naver -> userRepository.loginWithNaver(oAuth.token)
        }.map { user ->
            dataStoreRepository.saveUser(user)
            true
        }
    }

    suspend operator fun invoke(email: String, password: String): Flow<Boolean> {
        return userRepository.login(email, password)
            .map { userInfo ->
                dataStoreRepository.saveUser(userInfo)
                true
            }
    }
}
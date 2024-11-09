package com.ssafy.login

import com.ssafy.data.repository.auth.AuthRepository
import com.ssafy.datastore.DataStoreRepository
import javax.inject.Inject

class AutoLogInUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(isOnline: Boolean): Boolean = if (isOnline) {
        val refreshToken = dataStoreRepository.getRefreshToken()
        runCatching { authRepository.reissue(refreshToken) }.fold(
            onSuccess = { token ->
                token?.let {
                    dataStoreRepository.saveAccessToken(it)
                    return@fold true
                } ?: return@fold false
            },
            onFailure = {
                return@fold false
            }
        )
    } else {
        val user = dataStoreRepository.getUser()
        !(user.memberId == 0L && user.nickname.isEmpty() && user.email.isEmpty())
    }
}

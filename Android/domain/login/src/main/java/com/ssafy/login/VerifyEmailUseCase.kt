package com.ssafy.login

import com.ssafy.data.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyEmailUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(email: String, code: String): Flow<Unit> {
        return userRepository.verifyEmailCode(email, code)
    }

}

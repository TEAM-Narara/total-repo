package com.ssafy.login

import com.ssafy.data.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SendEmailUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(email: String): Flow<Unit> = flow {
        if (!isEmailValid(email)) throw RuntimeException("이메일 형식이 올바르지 않습니다.")
        userRepository.sendEmailCode(email).collect { emit(it) }
    }


    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}

package com.ssafy.login

import com.ssafy.data.repository.user.UserRepository
import com.ssafy.model.user.User
import com.ssafy.model.user.signup.RegisterDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(registerDTO: RegisterDTO): Flow<User> {
        return userRepository.register(registerDTO)
    }

}

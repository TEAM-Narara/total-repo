package com.ssafy.login

import com.ssafy.data.repository.fcm.FcmRepository
import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.data.repository.user.UserRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.user.User
import com.ssafy.model.user.signup.RegisterDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val memberRepository: MemberRepository,
    private val fcmRepository: FcmRepository
) {

    suspend operator fun invoke(registerDTO: RegisterDTO): Flow<Unit> {
        return userRepository.register(registerDTO).map { user: User ->
            dataStoreRepository.saveUser(user)
            memberRepository.addMember(user)
            fcmRepository.registerFcmToken(user.memberId)
        }
    }

}

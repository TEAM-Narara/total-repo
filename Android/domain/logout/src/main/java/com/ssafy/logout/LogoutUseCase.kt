package com.ssafy.logout

import com.ssafy.data.repository.clear.ClearRoomRepository
import com.ssafy.data.repository.fcm.FcmRepository
import com.ssafy.data.repository.user.UserRepository
import com.ssafy.datastore.DataStoreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val userRepository: UserRepository,
    private val fcmRepository: FcmRepository,
    private val clearRoomRepository: ClearRoomRepository
) {

    suspend operator fun invoke(): Flow<Unit> = flow {
        dataStoreRepository.clearAll()
        clearRoomRepository.clearAll()
        emit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun logout(): Flow<Unit> {
        val memberId = dataStoreRepository.getUser().memberId
        return fcmRepository.deleteFcmToken(memberId).also {
            userRepository.logout().flatMapConcat {
                invoke()
            }.collect()
        }
    }
}

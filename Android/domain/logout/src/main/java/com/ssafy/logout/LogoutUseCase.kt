package com.ssafy.logout

import com.ssafy.data.repository.user.UserRepository
import com.ssafy.datastore.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val userRepository: UserRepository,
) {
    // TODO map 뒤에 Room DB 지우는 로직 추가해야함.
    suspend operator fun invoke(): Flow<Unit> {
        return userRepository.logout()
            .map { dataStoreRepository.clearAll() }
    }
}
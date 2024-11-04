package com.ssafy.logout

import com.ssafy.data.repository.user.UserRepository
import com.ssafy.datastore.DataStoreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val userRepository: UserRepository,
) {
    // TODO map 뒤에 Room DB 지우는 로직 추가해야함.
    suspend operator fun invoke(): Flow<Unit> = flow {
        dataStoreRepository.clearAll()
        // room db clear

        emit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun logout(): Flow<Unit> {
        return userRepository.logout().flatMapConcat {
            invoke()
        }
    }
}

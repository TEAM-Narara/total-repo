package com.ssafy.fcm

import com.ssafy.data.repository.fcm.FcmRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.fcm.FcmDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterFcmTokenUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val fmcRepository: FcmRepository
) {
    suspend operator fun invoke(): Flow<FcmDTO> {
        val memberId = dataStoreRepository.getUser().memberId
        return fmcRepository.registerFcmToken(memberId)
    }
}

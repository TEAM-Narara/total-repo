package com.ssafy.fcm

import com.ssafy.data.repository.fcm.FcmRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.fcm.FcmDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val fcmRepository: FcmRepository
) {
    suspend operator fun invoke(fcmToken: String): Flow<FcmDTO> = flow {
        val memberId = dataStoreRepository.getUser().memberId
        if (memberId == 0L) return@flow
        fcmRepository.updateFcmToken(memberId, fcmToken).collect { emit(it) }
    }
}

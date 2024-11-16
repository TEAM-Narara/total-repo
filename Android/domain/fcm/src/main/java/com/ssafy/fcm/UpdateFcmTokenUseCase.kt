package com.ssafy.fcm

import com.ssafy.data.repository.fcm.FcmRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.fcm.FcmDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val fcmRepository: FcmRepository
) {
    suspend operator fun invoke(fcmToken: String): Flow<FcmDTO> {
        val memberId = dataStoreRepository.getUser().memberId
        return fcmRepository.updateFcmToken(memberId, fcmToken)
    }
}

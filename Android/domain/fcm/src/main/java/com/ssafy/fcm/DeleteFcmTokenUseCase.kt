package com.ssafy.fcm

import com.ssafy.data.repository.fcm.FcmRepository
import com.ssafy.datastore.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteFcmTokenUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val fcmRepository: FcmRepository
) {
    suspend operator fun invoke(): Flow<Unit> {
        val memberId = dataStoreRepository.getUser().memberId
        return fcmRepository.deleteFcmToken(memberId)
    }
}

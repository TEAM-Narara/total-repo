package com.ssafy.data.repository.fcm

import com.ssafy.model.fcm.FcmDTO
import com.ssafy.model.fcm.FcmMessageResponse
import com.ssafy.network.source.fcm.FcmDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmRepositoryImpl @Inject constructor(
    private val fcmDataSource: FcmDataSource
) : FcmRepository {

    override suspend fun registerFcmToken(memberId: Long): Flow<FcmDTO> =
        fcmDataSource.registerFcmToken(memberId)

    override suspend fun updateFcmToken(memberId: Long, fcmToken: String): Flow<FcmDTO> =
        fcmDataSource.updateFcmToken(memberId, fcmToken)

    override suspend fun deleteFcmToken(memberId: Long): Flow<Unit> =
        fcmDataSource.deleteFcmToken(memberId)


    override suspend fun getMyAlarmList(): Flow<List<FcmMessageResponse>> =
        fcmDataSource.getMyAlarmList()

}

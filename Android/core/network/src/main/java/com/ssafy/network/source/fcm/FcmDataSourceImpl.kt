package com.ssafy.network.source.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.model.fcm.FcmDTO
import com.ssafy.model.fcm.FcmMessageResponse
import com.ssafy.network.api.FcmAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FcmDataSourceImpl @Inject constructor(private val fcmAPI: FcmAPI) : FcmDataSource {

    override suspend fun registerFcmToken(memberId: Long): Flow<FcmDTO> {
        val fcmToken = FirebaseMessaging.getInstance().token.await()
        return safeApiCall { fcmAPI.registerFcmToken(memberId, fcmToken) }.toFlow()
    }

    override suspend fun updateFcmToken(memberId: Long, fcmToken: String): Flow<FcmDTO> =
        safeApiCall { fcmAPI.updateFcmToken(memberId, fcmToken) }.toFlow()

    override suspend fun deleteFcmToken(memberId: Long): Flow<Unit> =
        safeApiCall { fcmAPI.deleteFcmToken(memberId) }.toFlow()

    override suspend fun getMyAlarmList(): Flow<List<FcmMessageResponse>> =
        safeApiCall { fcmAPI.getMyAlarmList() }.toFlow()

}

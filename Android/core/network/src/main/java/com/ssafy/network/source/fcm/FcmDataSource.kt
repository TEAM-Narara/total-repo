package com.ssafy.network.source.fcm

import com.ssafy.model.fcm.FcmDTO
import com.ssafy.model.fcm.FcmMessageResponse
import kotlinx.coroutines.flow.Flow

interface FcmDataSource {

    suspend fun registerFcmToken(memberId: Long): Flow<FcmDTO>

    suspend fun updateFcmToken(memberId: Long, fcmToken: String): Flow<FcmDTO>

    suspend fun deleteFcmToken(memberId: Long): Flow<Unit>

    suspend fun getMyAlarmList(): Flow<List<FcmMessageResponse>>

}

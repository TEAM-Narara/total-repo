package com.ssafy.fcm

import com.ssafy.data.repository.fcm.FcmRepository
import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.model.fcm.FcmMessageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GeyMyAlarmListUseCase @Inject constructor(
    private val fcmRepository: FcmRepository,
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(): Flow<List<FcmMessageResponse>> {
        return fcmRepository.getMyAlarmList().map { alarmList ->
            alarmList.map { response: FcmMessageResponse ->
                response.manOfActionId?.let {
                    val memberUrl = memberRepository.getMember(it).firstOrNull()?.profileImgUrl
                    response.copy(manOfActionUrl = memberUrl)
                } ?: response
            }
        }
    }
}

package com.ssafy.member

import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.background.Cover
import com.ssafy.model.background.toCoverDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateMemberBackgroundUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val memberRepository: MemberRepository
) {

    suspend operator fun invoke(cover: Cover, isConnected: Boolean): Flow<Long> {
        val memberId = dataStoreRepository.getUser().memberId
        val coverDto = cover.toCoverDto()
        return memberRepository.createMemberBackground(memberId, coverDto, isConnected)
    }
}
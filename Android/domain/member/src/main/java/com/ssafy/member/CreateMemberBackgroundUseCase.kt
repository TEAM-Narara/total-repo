package com.ssafy.member

import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.background.Cover
import com.ssafy.model.background.toCoverDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class CreateMemberBackgroundUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val memberRepository: MemberRepository
) {

    suspend operator fun invoke(cover: Cover, isConnected: Boolean): Flow<Long> {
        val memberId = dataStoreRepository.getUser().memberId
        val coverDto = cover.toCoverDto()
        val prevBackground = memberRepository.getMemberBackground(coverDto.id)

        return if (prevBackground != null) {
            memberRepository.createMemberBackground(memberId, coverDto, isConnected)
        }else{
            flowOf(coverDto.id)
        }
    }
}
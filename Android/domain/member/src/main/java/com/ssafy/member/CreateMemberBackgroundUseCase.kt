package com.ssafy.member

import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.background.Cover
import com.ssafy.model.background.toCoverDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import kotlin.random.Random

class CreateMemberBackgroundUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val memberRepository: MemberRepository
) {

    suspend operator fun invoke(cover: Cover, isConnected: Boolean): Flow<Long> {
        val memberId = dataStoreRepository.getUser().memberId
        val coverDto = cover.toCoverDto()
        val prevBackgroundList =
            memberRepository.getLocalCreateMemberBackgrounds().map { it.imgPath }

        return if (prevBackgroundList.contains(coverDto.imgPath)) flowOf(coverDto.id)
        else memberRepository.createMemberBackground(
            memberId,
            coverDto.copy(id = -1L * Random.nextLong()),
            isConnected
        )
    }
}

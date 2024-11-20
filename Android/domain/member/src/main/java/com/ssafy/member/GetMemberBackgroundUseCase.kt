package com.ssafy.member

import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.background.Cover
import com.ssafy.model.background.toCover
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMemberBackgroundUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val memberRepository: MemberRepository
) {

    suspend operator fun invoke(isConnected: Boolean): Flow<List<Cover>> {
        val memberId = dataStoreRepository.getUser().memberId
        return memberRepository.getAllMemberBackgrounds(memberId, isConnected).map {
            it.map { coverDto -> coverDto.toCover() }
        }
    }

}
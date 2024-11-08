package com.ssafy.member

import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.datastore.DataStoreRepository
import com.ssafy.model.member.MemberUpdateRequestDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateMemberUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val memberRepository: MemberRepository
) {

    suspend operator fun invoke(
        memberUpdateRequestDto: MemberUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit> {
        val memberId = dataStoreRepository.getUser().memberId
        return memberRepository.updateMember(memberId, memberUpdateRequestDto, isConnected)
    }

}

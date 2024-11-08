package com.ssafy.member

import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.model.user.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMemberUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {

    suspend operator fun invoke(memberId: Long): Flow<User> {
        return memberRepository.getMember(memberId)
    }

}
package com.ssafy.member

import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.model.background.Cover
import com.ssafy.model.background.toCover
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMemberBackgroundUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {

    suspend operator fun invoke(): Flow<List<Cover>> {
        return memberRepository.getAllMemberBackgrounds().map {
            it.map { coverDto -> coverDto.toCover() }
        }
    }

}
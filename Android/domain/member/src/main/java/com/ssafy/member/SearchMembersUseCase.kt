package com.ssafy.member

import androidx.paging.PagingData
import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.model.user.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMembersUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {

    suspend operator fun invoke(keyword: String, sort: List<String>): Flow<PagingData<User>> {
        return memberRepository.searchMembers(keyword, sort)
    }

}

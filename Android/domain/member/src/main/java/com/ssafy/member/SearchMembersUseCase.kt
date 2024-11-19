package com.ssafy.member

import androidx.paging.PagingData
import androidx.paging.map
import com.ssafy.data.repository.member.MemberRepository
import com.ssafy.member.data.UserData
import com.ssafy.member.data.toUserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchMembersUseCase @Inject constructor(private val memberRepository: MemberRepository) {

    suspend operator fun invoke(
        keyword: String,
        filterMemberList: List<Long>,
        sort: List<String> = emptyList()
    ): Flow<PagingData<UserData>> {
        return memberRepository.searchMembers(keyword, sort, filterMemberList)
            .map { pager -> pager.map { user -> user.toUserData() } }
    }

}

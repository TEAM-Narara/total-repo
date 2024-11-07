package com.ssafy.network.source.member

import com.ssafy.model.background.BackgroundDto
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.member.PageDto
import com.ssafy.model.member.SearchMemberResponse
import com.ssafy.model.user.User
import kotlinx.coroutines.flow.Flow

interface MemberDataSource {

    suspend fun getMembers(): Flow<User>

    suspend fun updateMember(memberUpdateRequestDto: MemberUpdateRequestDto): Flow<Unit>

    suspend fun searchMembers(keyword: String, pageDto: PageDto): Flow<SearchMemberResponse>

    suspend fun createMemberBackground(
        background: BackgroundDto,
    ): Flow<Long>

    suspend fun deleteMemberBackground(id: Long): Flow<Unit>
}

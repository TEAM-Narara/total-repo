package com.ssafy.network.source.member

import com.ssafy.model.background.CoverDto
import com.ssafy.model.member.MemberBackgroundDto
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.member.PageDto
import com.ssafy.model.member.SearchMemberResponse
import com.ssafy.model.user.User
import kotlinx.coroutines.flow.Flow

interface MemberDataSource {

    suspend fun getMembers(): Flow<User>

    suspend fun updateMember(
        memberId: Long,
        memberUpdateRequestDto: MemberUpdateRequestDto
    ): Flow<Unit>

    suspend fun searchMembers(keyword: String, pageDto: PageDto): Flow<SearchMemberResponse>

    suspend fun getAllBackgrounds(memberId: Long): Flow<List<MemberBackgroundDto>>

    suspend fun createMemberBackground(
        memberId: Long,
        background: CoverDto
    ): Flow<MemberBackgroundDto>

    suspend fun deleteMemberBackground(memberId: Long, backgroundId: Long): Flow<Unit>
}

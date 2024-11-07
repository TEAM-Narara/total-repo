package com.ssafy.network.source.member

import com.ssafy.model.background.CoverDto
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.member.PageDto
import com.ssafy.model.user.User
import kotlinx.coroutines.flow.Flow

interface MemberDataSource {

    suspend fun getMembers(): Flow<User>

    suspend fun updateMember(memberUpdateRequestDto: MemberUpdateRequestDto): Flow<Unit>

    suspend fun searchMembers(keyword: String, pageDto: PageDto): Flow<List<User>>

    suspend fun createMemberBackground(
        background: CoverDto,
    ): Flow<Long>

    suspend fun deleteMemberBackground(id: Long): Flow<Unit>
}

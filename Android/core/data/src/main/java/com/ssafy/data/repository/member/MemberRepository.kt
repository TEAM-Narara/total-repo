package com.ssafy.data.repository.member

import androidx.paging.PagingData
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.user.User
import kotlinx.coroutines.flow.Flow

interface MemberRepository {

    suspend fun getMember(): Flow<User>

    suspend fun updateMember(
        memberUpdateRequestDto: MemberUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun searchMembers(keyword: String, sort: List<String>): Flow<PagingData<User>>

}
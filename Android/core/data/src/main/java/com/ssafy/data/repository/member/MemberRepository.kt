package com.ssafy.data.repository.member

import androidx.paging.PagingData
import com.ssafy.model.background.CoverDto
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.user.User
import kotlinx.coroutines.flow.Flow

interface MemberRepository {

    suspend fun getMember(memberId: Long): Flow<User>?

    suspend fun updateMember(
        memberId: Long,
        memberUpdateRequestDto: MemberUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun searchMembers(
        keyword: String,
        sort: List<String>,
        filterList: List<Long>
    ): Flow<PagingData<User>>

    suspend fun getLocalCreateMemberBackgrounds(): List<CoverDto>

    suspend fun getLocalOperationMemberBackgrounds(): List<CoverDto>

    suspend fun getMemberBackground(id: Long): CoverDto?

    suspend fun getAllMemberBackgrounds(): Flow<List<CoverDto>>

    suspend fun createMemberBackground(background: CoverDto, isConnected: Boolean): Flow<Long>

    suspend fun deleteMemberBackground(id: Long, isConnected: Boolean): Flow<Unit>
}
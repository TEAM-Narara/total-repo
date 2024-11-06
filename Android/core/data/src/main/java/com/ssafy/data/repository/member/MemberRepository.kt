package com.ssafy.data.repository.member

import androidx.paging.PagingData
import com.ssafy.database.dto.MemberBackgroundEntity
import com.ssafy.model.background.BackgroundDto
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.user.User
import com.ssafy.model.with.WorkspaceInBoardDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import kotlinx.coroutines.flow.Flow

interface MemberRepository {

    suspend fun getMember(memberId: Long): Flow<User>

    suspend fun updateMember(
        memberUpdateRequestDto: MemberUpdateRequestDto,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun searchMembers(keyword: String, sort: List<String>): Flow<PagingData<User>>

    suspend fun getLocalCreateMemberBackgrounds(): List<BackgroundDto>

    suspend fun getLocalOperationMemberBackgrounds(): List<BackgroundDto>

    suspend fun getMemberBackground(id: Long): BackgroundDto

    suspend fun getAllMemberBackgrounds(): Flow<List<BackgroundDto>>

    suspend fun createMemberBackground(background: BackgroundDto, isConnected: Boolean): Flow<Long>

    suspend fun deleteMemberBackground(id: Long, isConnected: Boolean): Flow<Unit>
}
package com.ssafy.network.source.workspace

import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.member.DetailMemberDto
import com.ssafy.model.member.SimpleMemberDto
import com.ssafy.model.workspace.WorkSpaceDTO
import kotlinx.coroutines.flow.Flow

interface WorkspaceDataSource {

    suspend fun getWorkspaceList(): Flow<List<WorkSpaceDTO>>

    suspend fun createWorkspace(name: String): Flow<WorkSpaceDTO>

    suspend fun deleteWorkspace(workspaceId: Long): Flow<Unit>

    suspend fun updateWorkspace(
        workspaceId: Long,
        name: String
    ): Flow<Unit>

    suspend fun getWorkspaceMembers(workspaceId: Long): Flow<List<MemberResponseDTO>>

    suspend fun getWorkspacesByMember(memberId: Long): Flow<List<WorkSpaceDTO>>

    suspend fun addWorkspaceMember(
        workspaceId: Long,
        simpleMemberDto: SimpleMemberDto
    ): Flow<DetailMemberDto>

    suspend fun deleteWorkspaceMember(
        workspaceId: Long,
        memberId: Long
    ): Flow<DetailMemberDto>

    suspend fun updateWorkspaceMember(
        workspaceId: Long,
        detailMemberDto: SimpleMemberDto
    ): Flow<DetailMemberDto>

}

package com.ssafy.network.source.workspace

import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import kotlinx.coroutines.flow.Flow

interface WorkspaceDataSource {

    suspend fun getWorkspaceList(): Flow<List<WorkSpaceDTO>>

    suspend fun createWorkspace(name: String): Flow<Long>

    suspend fun deleteWorkspace(workspaceId: Long): Flow<Unit>

    suspend fun updateWorkspace(
        workspaceId: Long,
        name: String
    ): Flow<Unit>

    suspend fun getWorkspaceMembers(workspaceId: Long): Flow<List<MemberResponseDTO>>

    suspend fun getWorkspacesByMember(memberId: Long): Flow<List<WorkSpaceDTO>>

    suspend fun deleteWorkspaceMember(id: Long): Flow<Unit>

    suspend fun updateWorkspaceMember(
        id: Long,
        authority: String,
    ): Flow<Unit>
}
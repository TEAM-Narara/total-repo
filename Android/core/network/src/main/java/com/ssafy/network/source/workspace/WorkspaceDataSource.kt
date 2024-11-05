package com.ssafy.network.source.workspace

import com.ssafy.model.board.MemberListResponseDTO
import com.ssafy.model.workspace.WorkSpaceListResponseDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import kotlinx.coroutines.flow.Flow

interface WorkspaceDataSource {

    suspend fun getWorkspaceList(): Flow<WorkSpaceListResponseDTO>

    suspend fun createWorkspace(workspaceRequestDTO: WorkspaceRequestDTO): Flow<Unit>

    suspend fun deleteWorkspace(workspaceId: Long): Flow<Unit>

    suspend fun updateWorkspace(
        workspaceId: Long,
        workspaceRequestDTO: WorkspaceRequestDTO
    ): Flow<Unit>

    suspend fun getWorkspaceMembers(workspaceId: Long): Flow<MemberListResponseDTO>

    suspend fun getWorkspacesByMember(memberId: Long): Flow<WorkSpaceListResponseDTO>

}

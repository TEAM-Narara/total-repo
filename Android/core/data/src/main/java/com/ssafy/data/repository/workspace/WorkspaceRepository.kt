package com.ssafy.data.repository.workspace

import com.ssafy.model.workspace.WorkSpaceListResponseDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import kotlinx.coroutines.flow.Flow

interface WorkspaceRepository {

    suspend fun getWorkspaceList(isConnected: Boolean): Flow<WorkSpaceListResponseDTO>

    suspend fun createWorkspace(
        workspaceRequestDTO: WorkspaceRequestDTO,
        isConnected: Boolean
    ): Flow<Unit>

    suspend fun deleteWorkspace(workspaceId: Long, isConnected: Boolean): Flow<Unit>

    suspend fun updateWorkspace(
        workspaceId: Long,
        workspaceRequestDTO: WorkspaceRequestDTO,
        isConnected: Boolean
    ): Flow<Unit>

}

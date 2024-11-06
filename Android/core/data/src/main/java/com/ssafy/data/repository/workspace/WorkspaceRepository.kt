package com.ssafy.data.repository.workspace

import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.with.WorkspaceInBoardDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import kotlinx.coroutines.flow.Flow

interface WorkspaceRepository {

    suspend fun getWorkspaceList(isConnected: Boolean): Flow<List<WorkSpaceDTO>>

    suspend fun getLocalScreenWorkspaceList(): Flow<List<WorkSpaceDTO>>

    suspend fun getLocalCreateWorkspaceList(): Flow<List<WorkspaceInBoardDTO>>

    suspend fun getLocalOperationWorkspaceList(): Flow<List<WorkSpaceDTO>>

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

    suspend fun getWorkspaceMembers(workspaceId: Long): Flow<List<MemberResponseDTO>>

    suspend fun getWorkspacesByMember(memberId: Long): Flow<List<WorkSpaceDTO>>

}

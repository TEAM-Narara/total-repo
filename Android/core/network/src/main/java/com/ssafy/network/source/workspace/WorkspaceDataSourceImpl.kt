package com.ssafy.network.source.workspace

import com.ssafy.model.board.MemberListResponseDTO
import com.ssafy.model.workspace.WorkSpaceListResponseDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import com.ssafy.network.api.WorkspaceAPI
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkspaceDataSourceImpl @Inject constructor(
    private val workspaceAPI: WorkspaceAPI
) : WorkspaceDataSource {

    override suspend fun getWorkspaceList(): Flow<WorkSpaceListResponseDTO> =
        workspaceAPI.getWorkspaces().toFlow()

    override suspend fun createWorkspace(workspaceRequestDTO: WorkspaceRequestDTO): Flow<Unit> =
        workspaceAPI.createWorkspace(workspaceRequestDTO).toFlow()

    override suspend fun deleteWorkspace(workspaceId: Long): Flow<Unit> =
        workspaceAPI.deleteWorkspace(workspaceId).toFlow()

    override suspend fun updateWorkspace(
        workspaceId: Long,
        workspaceRequestDTO: WorkspaceRequestDTO
    ): Flow<Unit> =
        workspaceAPI.updateWorkspace(workspaceId, workspaceRequestDTO).toFlow()


    override suspend fun getWorkspaceMembers(workspaceId: Long): Flow<MemberListResponseDTO> =
        workspaceAPI.getWorkspaceMembers(workspaceId).toFlow()

    override suspend fun getWorkspacesByMember(memberId: Long): Flow<WorkSpaceListResponseDTO> =
        workspaceAPI.getWorkspacesByMember(memberId).toFlow()

}

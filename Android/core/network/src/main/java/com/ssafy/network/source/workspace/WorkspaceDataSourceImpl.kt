package com.ssafy.network.source.workspace

import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import com.ssafy.network.api.WorkspaceAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WorkspaceDataSourceImpl @Inject constructor(
    private val workspaceAPI: WorkspaceAPI
) : WorkspaceDataSource {

    override suspend fun getWorkspaceList(): Flow<List<WorkSpaceDTO>> =
        safeApiCall { workspaceAPI.getWorkspaces() }.toFlow()

    override suspend fun createWorkspace(name: String): Flow<Long> =
        safeApiCall { workspaceAPI.createWorkspace(name) }.toFlow()

    override suspend fun deleteWorkspace(workspaceId: Long): Flow<Unit> =
        safeApiCall { workspaceAPI.deleteWorkspace(workspaceId) }.toFlow()

    override suspend fun updateWorkspace(
        workspaceId: Long,
        name: String
    ): Flow<Unit> =
        safeApiCall { workspaceAPI.updateWorkspace(workspaceId, name) }.toFlow()

    override suspend fun getWorkspaceMembers(workspaceId: Long): Flow<List<MemberResponseDTO>> =
        safeApiCall { workspaceAPI.getWorkspaceMembers(workspaceId) }.toFlow()

    override suspend fun getWorkspacesByMember(memberId: Long): Flow<List<WorkSpaceDTO>> =
        safeApiCall { workspaceAPI.getWorkspacesByMember(memberId) }.toFlow()

    override suspend fun deleteWorkspaceMember(id: Long): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateWorkspaceMember(id: Long, authority: String): Flow<Unit> {
        TODO("Not yet implemented")
    }
}

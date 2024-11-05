package com.ssafy.network.source.workspace

import com.ssafy.model.board.MemberListResponseDTO
import com.ssafy.model.workspace.WorkSpaceListResponseDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import com.ssafy.network.api.WorkspaceAPI
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import javax.inject.Inject

class WorkspaceDataSourceImpl @Inject constructor(
    private val workspaceAPI: WorkspaceAPI
) : WorkspaceDataSource {

    override suspend fun getWorkspaceList(): Response<ApiResponse<WorkSpaceListResponseDTO>> =
        workspaceAPI.getWorkspaces()

    override suspend fun createWorkspace(workspaceRequestDTO: WorkspaceRequestDTO): Response<ApiResponse<Unit>> =
        workspaceAPI.createWorkspace(workspaceRequestDTO)

    override suspend fun deleteWorkspace(workspaceId: Long): Response<ApiResponse<Unit>> =
        workspaceAPI.deleteWorkspace(workspaceId)

    override suspend fun updateWorkspace(
        workspaceId: Long,
        workspaceRequestDTO: WorkspaceRequestDTO
    ): Response<ApiResponse<Unit>> =
        workspaceAPI.updateWorkspace(workspaceId, workspaceRequestDTO)


    override suspend fun getWorkspaceMembers(workspaceId: Long): Response<ApiResponse<MemberListResponseDTO>> =
        workspaceAPI.getWorkspaceMembers(workspaceId)

    override suspend fun getWorkspacesByMember(memberId: Long): Response<ApiResponse<WorkSpaceListResponseDTO>> =
        workspaceAPI.getWorkspacesByMember(memberId)

}

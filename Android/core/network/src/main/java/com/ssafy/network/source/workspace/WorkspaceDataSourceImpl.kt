package com.ssafy.network.source.workspace

import com.ssafy.model.workspace.WorkSpaceListResponseDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import com.ssafy.model.workspace.WorkspaceResponseDto
import com.ssafy.network.api.WorkspaceAPI
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import javax.inject.Inject

class WorkspaceDataSourceImpl @Inject constructor(
    private val workspaceAPI: WorkspaceAPI
) : WorkspaceDataSource {

    override suspend fun getWorkspaceList(): Response<ApiResponse<WorkSpaceListResponseDTO>> =
        workspaceAPI.getWorkspaces()

    override suspend fun createWorkspace(workspaceRequestDTO: WorkspaceRequestDTO): Response<ApiResponse<WorkspaceResponseDto>> =
        workspaceAPI.createWorkspace(workspaceRequestDTO)

    override suspend fun deleteWorkspace(workspaceId: Long): Response<ApiResponse<Unit>> =
        workspaceAPI.deleteWorkspace(workspaceId)

    override suspend fun updateWorkspace(
        workspaceId: Long,
        workspaceRequestDTO: WorkspaceRequestDTO
    ): Response<ApiResponse<WorkspaceResponseDto>> =
        workspaceAPI.updateWorkspace(workspaceId, workspaceRequestDTO)

}

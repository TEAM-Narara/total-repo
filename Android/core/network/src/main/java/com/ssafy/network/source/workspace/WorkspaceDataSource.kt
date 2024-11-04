package com.ssafy.network.source.workspace

import com.ssafy.model.board.MemberListResponseDTO
import com.ssafy.model.workspace.WorkSpaceListResponseDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import com.ssafy.model.workspace.WorkspaceResponseDto
import com.ssafy.network.source.ApiResponse
import retrofit2.Response

interface WorkspaceDataSource {

    suspend fun getWorkspaceList(): Response<ApiResponse<WorkSpaceListResponseDTO>>

    suspend fun createWorkspace(workspaceRequestDTO: WorkspaceRequestDTO): Response<ApiResponse<WorkspaceResponseDto>>

    suspend fun deleteWorkspace(workspaceId: Long): Response<ApiResponse<Unit>>

    suspend fun updateWorkspace(
        workspaceId: Long,
        workspaceRequestDTO: WorkspaceRequestDTO
    ): Response<ApiResponse<WorkspaceResponseDto>>

    suspend fun getWorkspaceMembers(workspaceId: Long): Response<ApiResponse<MemberListResponseDTO>>

    suspend fun getWorkspacesByMember(memberId: Long): Response<ApiResponse<WorkSpaceListResponseDTO>>

}

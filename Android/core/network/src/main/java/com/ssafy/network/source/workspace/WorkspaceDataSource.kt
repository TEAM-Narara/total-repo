package com.ssafy.network.source.workspace

import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import com.ssafy.network.source.ApiResponse
import retrofit2.Response

interface WorkspaceDataSource {

    suspend fun getWorkspaceList(): Response<ApiResponse<List<WorkSpaceDTO>>>

    suspend fun createWorkspace(workspaceRequestDTO: WorkspaceRequestDTO): Response<ApiResponse<Unit>>

    suspend fun deleteWorkspace(workspaceId: Long): Response<ApiResponse<Unit>>

    suspend fun updateWorkspace(
        workspaceId: Long,
        workspaceRequestDTO: WorkspaceRequestDTO
    ): Response<ApiResponse<Unit>>

    suspend fun getWorkspaceMembers(workspaceId: Long): Response<ApiResponse<List<MemberResponseDTO>>>

    suspend fun getWorkspacesByMember(memberId: Long): Response<ApiResponse<List<WorkSpaceDTO>>>

}

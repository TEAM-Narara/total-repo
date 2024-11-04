package com.ssafy.network.api

import com.ssafy.model.workspace.WorkSpaceListResponseDTO
import com.ssafy.model.workspace.WorkspaceRequestDTO
import com.ssafy.model.workspace.WorkspaceResponseDto
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface WorkspaceAPI {

    @GET("api/v1/workspaces")
    suspend fun getWorkspaces(): Response<ApiResponse<WorkSpaceListResponseDTO>>

    @POST("api/v1/workspaces")
    suspend fun createWorkspace(@Body workspaceRequestDTO: WorkspaceRequestDTO): Response<ApiResponse<WorkspaceResponseDto>>

    @DELETE("api/v1/workspaces/{workspaceId}")
    suspend fun deleteWorkspace(@Path("workspaceId") workspaceId: Long): Response<ApiResponse<Unit>>

    @PATCH("api/v1/workspaces/{workspaceId}")
    suspend fun updateWorkspace(
        @Path("workspaceId") workspaceId: Long,
        @Body workspaceRequestDTO: WorkspaceRequestDTO
    ): Response<ApiResponse<WorkspaceResponseDto>>

}

package com.ssafy.network.api

import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.member.DetailMemberDto
import com.ssafy.model.member.SimpleMemberDto
import com.ssafy.model.workspace.WorkSpaceDTO
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkspaceAPI {

    @GET("api/v1/workspaces")
    suspend fun getWorkspaces(): Response<ApiResponse<List<WorkSpaceDTO>>>

    @POST("api/v1/workspaces")
    suspend fun createWorkspace(@Query("name") name: String): Response<ApiResponse<WorkSpaceDTO>>

    @DELETE("api/v1/workspaces/{workspaceId}")
    suspend fun deleteWorkspace(@Path("workspaceId") workspaceId: Long): Response<ApiResponse<Unit>>

    @PATCH("api/v1/workspaces/{workspaceId}")
    suspend fun updateWorkspace(
        @Path("workspaceId") workspaceId: Long,
        @Body name: String
    ): Response<ApiResponse<Unit>>

    @GET("api/v1/{workspaceId}/members")
    suspend fun getWorkspaceMembers(@Path("workspaceId") workspaceId: Long): Response<ApiResponse<List<MemberResponseDTO>>>

    @POST("api/v1/{workspaceId}/members")
    suspend fun addWorkspaceMember(
        @Path("workspaceId") workspaceId: Long,
        @Body simpleMemberDto: SimpleMemberDto
    ): Response<ApiResponse<DetailMemberDto>>

    @DELETE("api/v1/{workspaceId}/members")
    suspend fun deleteWorkspaceMember(
        @Path("workspaceId") workspaceId: Long,
        @Body memberId: Long
    ): Response<ApiResponse<DetailMemberDto>>

    @PATCH("api/v1/{workspaceId}/members")
    suspend fun updateWorkspaceMember(
        @Path("workspaceId") workspaceId: Long,
        @Body detailMemberDto: SimpleMemberDto
    ): Response<ApiResponse<DetailMemberDto>>

    @GET("api/v1/members/{memberId}/workspaces")
    suspend fun getWorkspacesByMember(@Path("memberId") memberId: Long): Response<ApiResponse<List<WorkSpaceDTO>>>
}

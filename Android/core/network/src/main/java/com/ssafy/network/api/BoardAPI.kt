package com.ssafy.network.api

import com.ssafy.model.activity.BoardActivityDto
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.board.UpdateBoardRequestDto
import com.ssafy.model.member.SimpleMemberDto
import com.ssafy.network.source.ApiResponse
import com.ssafy.nullable.UpdateBoardWithNull
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BoardAPI {

    @POST("api/v1/boards")
    suspend fun createBoard(@Body boardDTO: BoardDTO): Response<ApiResponse<BoardDTO>>

    @GET("api/v1/boards/{boardId}")
    suspend fun getBoard(@Path("boardId") boardId: Long): Response<ApiResponse<BoardDTO>>

    @DELETE("api/v1/boards/{boardId}")
    suspend fun deleteBoard(@Path("boardId") boardId: Long): Response<ApiResponse<Unit>>

    @PATCH("api/v1/boards/{boardId}")
    suspend fun updateBoard(
        @Path("boardId") id: Long,
        @Body updateBoardRequestDto: UpdateBoardRequestDto
    ): Response<ApiResponse<Unit>>

    @PATCH("api/v1/boards/{boardId}")
    suspend fun updateBoardWithNull(
        @Path("boardId") id: Long,
        @Body updateBoardRequestDto: UpdateBoardWithNull
    ): Response<ApiResponse<Unit>>

    @PATCH("api/v1/boards/{boardId}/archive")
    suspend fun setBoardArchive(@Path("boardId") boardId: Long): Response<ApiResponse<Unit>>

    @GET("api/v1/boards/workspace/{workspaceId}")
    suspend fun getBoardsByWorkspace(@Path("workspaceId") workspaceId: Long): Response<ApiResponse<List<BoardDTO>>>

    @GET("api/v1/boards/workspace/{workspaceId}/archive")
    suspend fun getArchivedBoardsByWorkspace(@Path("workspaceId") workspaceId: Long): Response<ApiResponse<List<BoardDTO>>>

    @GET("api/v1/boards/{boardId}/member/watch-status")
    suspend fun getWatchStatus(@Path("boardId") boardId: Long): Response<ApiResponse<Boolean>>

    @PUT("api/v1/boards/{boardId}/member/watch-status")
    suspend fun toggleWatchBoard(@Path("boardId") boardId: Long): Response<ApiResponse<Unit>>

    @GET("api/v1/boards/{boardId}/members")
    suspend fun getBoardMembers(@Path("boardId") boardId: Long): Response<ApiResponse<List<MemberResponseDTO>>>

    @POST("api/v1/boards/{boardId}/member")
    suspend fun createBoardMember(
        @Path("boardId") boardId: Long,
        @Body memberId: Map<String, Long>
    ): Response<ApiResponse<MemberResponseDTO>>

    @DELETE("api/v1/boards/{boardId}/member")
    suspend fun deleteBoardMember(
        @Path("boardId") boardId: Long,
        @Body memberId: Map<String, Long>
    ): Response<ApiResponse<MemberResponseDTO>>

    @PATCH("api/v1/boards/{boardId}/member")
    suspend fun updateBoardMember(
        @Path("boardId") boardId: Long,
        @Body simpleMemberDto: SimpleMemberDto
    ): Response<ApiResponse<MemberResponseDTO>>

    @GET("api/v1/boards/{boardId}/activity")
    suspend fun getBoardActivity(
        @Path("boardId") boardId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<BoardActivityDto>>

}

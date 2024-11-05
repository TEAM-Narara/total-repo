package com.ssafy.network.api

import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.BoardDetailResponseDtoList
import com.ssafy.model.board.MemberListResponseDTO
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BoardAPI {

    @POST("api/v1/boards")
    suspend fun createBoard(): Response<ApiResponse<Unit>>

    @GET("api/v1/boards/{boardId}")
    suspend fun getBoard(@Path("boardId") id: Long): Response<ApiResponse<BoardDTO>>

    @DELETE("api/v1/boards/{boardId}")
    suspend fun deleteBoard(@Path("boardId") id: Long): Response<ApiResponse<Unit>>

    @PATCH("api/v1/boards/{boardId}")
    suspend fun updateBoard(
        @Path("boardId") id: Long,
        @Body boardDTO: BoardDTO
    ): Response<ApiResponse<Unit>>

    @PATCH("api/v1/boards/{boardId}/archive")
    suspend fun setBoardArchive(@Path("boardId") boardId: Long): Response<ApiResponse<Unit>>

    @GET("api/v1/boards/workspace/{workspaceId}")
    suspend fun getBoardsByWorkspace(@Path("workspaceId") workspaceId: Long): Response<ApiResponse<BoardDetailResponseDtoList>>

    @GET("api/v1/boards/workspace/{workspaceId}/archive")
    suspend fun getArchivedBoardsByWorkspace(@Path("workspaceId") workspaceId: Long): Response<ApiResponse<BoardDetailResponseDtoList>>

    @GET("api/v1/boards/{boardId}/member/watch-status")
    suspend fun getWatchStatus(@Path("boardId") boardId: Long): Response<ApiResponse<Boolean>>

    @PUT("api/v1/boards/{boardId}/member/watch-status")
    suspend fun toggleWatchBoard(@Path("boardId") boardId: Long): Response<ApiResponse<Unit>>

    @GET("api/v1/boards/{boardId}/members")
    suspend fun getBoardMembers(@Path("boardId") boardId: Long): Response<ApiResponse<MemberListResponseDTO>>

}

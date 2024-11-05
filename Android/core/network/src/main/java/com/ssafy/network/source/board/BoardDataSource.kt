package com.ssafy.network.source.board

import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.BoardDetailResponseDtoList
import com.ssafy.model.board.MemberListResponseDTO
import com.ssafy.network.source.ApiResponse
import retrofit2.Response

interface BoardDataSource {

    suspend fun createBoard(): Response<ApiResponse<Unit>>

    suspend fun getBoard(id: Long): Response<ApiResponse<BoardDTO>>

    suspend fun deleteBoard(id: Long): Response<ApiResponse<Unit>>

    suspend fun updateBoard(id: Long, boardDTO: BoardDTO): Response<ApiResponse<Unit>>

    suspend fun setBoardArchive(boardId: Long): Response<ApiResponse<Unit>>

    suspend fun getBoardsByWorkspace(workspaceId: Long): Response<ApiResponse<BoardDetailResponseDtoList>>

    suspend fun getArchivedBoardsByWorkspace(workspaceId: Long): Response<ApiResponse<BoardDetailResponseDtoList>>

    suspend fun getWatchStatus(boardId: Long): Response<ApiResponse<Boolean>>

    suspend fun toggleWatchBoard(boardId: Long): Response<ApiResponse<Unit>>

    suspend fun getBoardMembers(boardId: Long): Response<ApiResponse<MemberListResponseDTO>>

}

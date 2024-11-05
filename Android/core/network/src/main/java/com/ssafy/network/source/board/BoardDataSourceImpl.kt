package com.ssafy.network.source.board

import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.network.api.BoardAPI
import com.ssafy.network.source.ApiResponse
import retrofit2.Response
import javax.inject.Inject

class BoardDataSourceImpl @Inject constructor(private val boardAPI: BoardAPI) : BoardDataSource {

    override suspend fun createBoard(): Response<ApiResponse<Unit>> =
        boardAPI.createBoard()

    override suspend fun getBoard(id: Long): Response<ApiResponse<BoardDTO>> =
        boardAPI.getBoard(id)

    override suspend fun deleteBoard(id: Long): Response<ApiResponse<Unit>> =
        boardAPI.deleteBoard(id)

    override suspend fun updateBoard(
        id: Long,
        boardDTO: BoardDTO
    ): Response<ApiResponse<Unit>> = boardAPI.updateBoard(id, boardDTO)

    override suspend fun setBoardArchive(boardId: Long): Response<ApiResponse<Unit>> =
        boardAPI.setBoardArchive(boardId)

    override suspend fun getBoardsByWorkspace(workspaceId: Long): Response<ApiResponse<List<BoardDTO>>> =
        boardAPI.getBoardsByWorkspace(workspaceId)

    override suspend fun getArchivedBoardsByWorkspace(workspaceId: Long): Response<ApiResponse<List<BoardDTO>>> =
        boardAPI.getArchivedBoardsByWorkspace(workspaceId)

    override suspend fun getWatchStatus(boardId: Long): Response<ApiResponse<Boolean>> =
        boardAPI.getWatchStatus(boardId)

    override suspend fun toggleWatchBoard(boardId: Long): Response<ApiResponse<Unit>> =
        boardAPI.toggleWatchBoard(boardId)

    override suspend fun getBoardMembers(boardId: Long): Response<ApiResponse<List<MemberResponseDTO>>> =
        boardAPI.getBoardMembers(boardId)

}

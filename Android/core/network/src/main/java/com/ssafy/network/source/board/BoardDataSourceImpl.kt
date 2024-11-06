package com.ssafy.network.source.board

import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.network.api.BoardAPI
import com.ssafy.network.source.safeApiCall
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BoardDataSourceImpl @Inject constructor(private val boardAPI: BoardAPI) : BoardDataSource {

    override suspend fun createBoard(): Flow<Unit> = safeApiCall { boardAPI.createBoard() }.toFlow()

    override suspend fun getBoard(id: Long): Flow<BoardDTO> =
        safeApiCall { boardAPI.getBoard(id) }.toFlow() // TODO : Socket으로 바꾸기

    override suspend fun deleteBoard(id: Long): Flow<Unit> =
        safeApiCall { boardAPI.deleteBoard(id) }.toFlow()

    override suspend fun updateBoard(id: Long, boardDTO: BoardDTO): Flow<Unit> =
        safeApiCall { boardAPI.updateBoard(id, boardDTO) }.toFlow()

    override suspend fun setBoardArchive(boardId: Long): Flow<Unit> =
        safeApiCall { boardAPI.setBoardArchive(boardId) }.toFlow()

    override suspend fun getBoardsByWorkspace(workspaceId: Long): Flow<List<BoardDTO>> =
        safeApiCall { boardAPI.getBoardsByWorkspace(workspaceId) }.toFlow()

    override suspend fun getArchivedBoardsByWorkspace(workspaceId: Long): Flow<List<BoardDTO>> =
        safeApiCall { boardAPI.getArchivedBoardsByWorkspace(workspaceId) }.toFlow()

    override suspend fun getWatchStatus(boardId: Long): Flow<Boolean> =
        safeApiCall { boardAPI.getWatchStatus(boardId) }.toFlow()

    override suspend fun toggleWatchBoard(boardId: Long): Flow<Unit> =
        safeApiCall { boardAPI.toggleWatchBoard(boardId) }.toFlow()

    override suspend fun getBoardMembers(boardId: Long): Flow<List<MemberResponseDTO>> =
        safeApiCall { boardAPI.getBoardMembers(boardId) }.toFlow()

}

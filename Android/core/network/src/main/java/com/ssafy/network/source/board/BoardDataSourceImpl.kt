package com.ssafy.network.source.board

import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.BoardDetailResponseDtoList
import com.ssafy.model.board.MemberListResponseDTO
import com.ssafy.network.api.BoardAPI
import com.ssafy.network.source.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BoardDataSourceImpl @Inject constructor(private val boardAPI: BoardAPI) : BoardDataSource {

    override suspend fun createBoard(): Flow<Unit> = boardAPI.createBoard().toFlow()

    override suspend fun getBoard(id: Long): Flow<BoardDTO> =
        boardAPI.getBoard(id).toFlow() // TODO : Socket으로 바꾸기

    override suspend fun deleteBoard(id: Long): Flow<Unit> = boardAPI.deleteBoard(id).toFlow()

    override suspend fun updateBoard(id: Long, boardDTO: BoardDTO): Flow<Unit> =
        boardAPI.updateBoard(id, boardDTO).toFlow()

    override suspend fun setBoardArchive(boardId: Long): Flow<Unit> =
        boardAPI.setBoardArchive(boardId).toFlow()

    override suspend fun getBoardsByWorkspace(workspaceId: Long): Flow<BoardDetailResponseDtoList> =
        boardAPI.getBoardsByWorkspace(workspaceId).toFlow()

    override suspend fun getArchivedBoardsByWorkspace(workspaceId: Long): Flow<BoardDetailResponseDtoList> =
        boardAPI.getArchivedBoardsByWorkspace(workspaceId).toFlow()

    override suspend fun getWatchStatus(boardId: Long): Flow<Boolean> =
        boardAPI.getWatchStatus(boardId).toFlow()

    override suspend fun toggleWatchBoard(boardId: Long): Flow<Unit> =
        boardAPI.toggleWatchBoard(boardId).toFlow()

    override suspend fun getBoardMembers(boardId: Long): Flow<MemberListResponseDTO> =
        boardAPI.getBoardMembers(boardId).toFlow()

}

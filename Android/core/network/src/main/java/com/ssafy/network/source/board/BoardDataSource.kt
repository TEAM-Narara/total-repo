package com.ssafy.network.source.board

import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.BoardDetailResponseDtoList
import com.ssafy.model.board.MemberListResponseDTO
import kotlinx.coroutines.flow.Flow

interface BoardDataSource {

    suspend fun createBoard(): Flow<Unit>

    suspend fun getBoard(id: Long): Flow<BoardDTO>

    suspend fun deleteBoard(id: Long): Flow<Unit>

    suspend fun updateBoard(id: Long, boardDTO: BoardDTO): Flow<Unit>

    suspend fun setBoardArchive(boardId: Long): Flow<Unit>

    suspend fun getBoardsByWorkspace(workspaceId: Long): Flow<BoardDetailResponseDtoList>

    suspend fun getArchivedBoardsByWorkspace(workspaceId: Long): Flow<BoardDetailResponseDtoList>

    suspend fun getWatchStatus(boardId: Long): Flow<Boolean>

    suspend fun toggleWatchBoard(boardId: Long): Flow<Unit>

    suspend fun getBoardMembers(boardId: Long): Flow<MemberListResponseDTO>

}

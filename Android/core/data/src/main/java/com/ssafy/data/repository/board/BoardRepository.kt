package com.ssafy.data.repository.board

import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.board.UpdateBoardRequestDto
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    suspend fun createBoard(isConnected: Boolean): Flow<Unit>

    suspend fun getBoard(id: Long): Flow<BoardDTO>

    suspend fun deleteBoard(id: Long, isConnected: Boolean): Flow<Unit>

    suspend fun updateBoard(id: Long, updateBoardRequestDto: UpdateBoardRequestDto, isConnected: Boolean): Flow<Unit>

    suspend fun setBoardArchive(id: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getBoardsByWorkspace(id: Long): Flow<List<BoardDTO>>

    suspend fun getArchivedBoardsByWorkspace(id: Long): Flow<List<BoardDTO>>

    suspend fun getWatchStatus(id: Long): Flow<Boolean>

    suspend fun toggleBoardWatch(id: Long, isConnected: Boolean): Flow<Unit>

    suspend fun getBoardMembers(id: Long): Flow<List<MemberResponseDTO>>

}

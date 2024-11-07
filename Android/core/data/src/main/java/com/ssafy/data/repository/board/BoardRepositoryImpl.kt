package com.ssafy.data.repository.board

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.repository.toEntity
import com.ssafy.database.dao.BoardDao
import com.ssafy.database.dao.BoardMemberDao
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.database.dto.piece.toDto
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.board.UpdateBoardRequestDto
import com.ssafy.model.with.BoardInListDTO
import com.ssafy.model.with.BoardMemberAlarmDTO
import com.ssafy.model.with.BoardMemberDTO
import com.ssafy.model.with.DataStatus
import com.ssafy.model.with.ListMemberAlarmDTO
import com.ssafy.model.with.ListMemberDTO
import com.ssafy.network.source.board.BoardDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepositoryImpl @Inject constructor(
    private val boardDataSource: BoardDataSource,
    private val boardDao: BoardDao,
    private val boardMemberDao: BoardMemberDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BoardRepository {

    override suspend fun createBoard(boardDTO: BoardDTO, isConnected: Boolean): Flow<Long> =
        withContext(ioDispatcher) {
            if (isConnected) {
                boardDataSource.createBoard(boardDTO).map { 5 }
            } else {
                flowOf(boardDao.insertBoard(
                    boardDTO.copy(isStatus = DataStatus.CREATE).toEntity()
                ))
            }
        }

    override suspend fun getBoard(id: Long): Flow<BoardDTO> =
        withContext(ioDispatcher) {
            boardDao.getBoardFlow(id).map { it.toDto() }
        }

    override suspend fun deleteBoard(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val board = boardDao.getBoard(id)

            if(board != null) {
                if (isConnected) {
                    boardDataSource.deleteBoard(id)
                } else {
                    val result = when(board.isStatus) {
                        DataStatus.CREATE ->
                            boardDao.deleteBoard(board)
                        else ->
                            boardDao.updateBoard(board.copy(isStatus = DataStatus.DELETE))
                    }

                    flowOf(result)
                }
            } else{
                flowOf(Unit)
            }
        }

    override suspend fun updateBoard(id: Long, updateBoardRequestDto: UpdateBoardRequestDto, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val board = boardDao.getBoard(id)

            if(board != null) {
                if (isConnected) {
                    boardDataSource.updateBoard(id, updateBoardRequestDto)
                } else {
                    val result = when(board.isStatus) {
                        DataStatus.STAY ->
                            boardDao.updateBoard(board.copy(
                                name = updateBoardRequestDto.name,
                                backgroundType = updateBoardRequestDto.background.type.name,
                                backgroundValue = updateBoardRequestDto.background.value,
                                visibility = updateBoardRequestDto.visibility.name,
                                isStatus = DataStatus.UPDATE
                            ))
                        DataStatus.CREATE, DataStatus.UPDATE  ->
                            boardDao.updateBoard(board.copy(
                                name = updateBoardRequestDto.name,
                                backgroundType = updateBoardRequestDto.background.type.name,
                                backgroundValue = updateBoardRequestDto.background.value,
                                visibility = updateBoardRequestDto.visibility.name,
                            ))
                        DataStatus.DELETE -> { }
                    }

                    flowOf(result)
                }
            } else{
                flowOf(Unit)
            }
        }

    override suspend fun setBoardArchive(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val board = boardDao.getBoard(id)

            if(board != null) {
                if (isConnected) {
                    boardDataSource.setBoardArchive(id)
                } else {
                    val result = when(board.isStatus) {
                        DataStatus.STAY ->
                            boardDao.updateBoard(board.copy(
                                isClosed = !board.isClosed,
                                isStatus = DataStatus.UPDATE
                            ))
                        DataStatus.CREATE, DataStatus.UPDATE  ->
                            boardDao.updateBoard(board.copy(
                                isClosed = !board.isClosed,
                            ))
                        DataStatus.DELETE -> { }
                    }

                    flowOf(result)
                }
            } else{
                flowOf(Unit)
            }
        }

    override suspend fun getBoardsByWorkspace(id: Long): Flow<List<BoardDTO>> =
        withContext(ioDispatcher) {
            boardDao.getAllBoards(id)
                .map { entities -> entities.map { it.toDto() } }
        }

    override suspend fun getLocalCreateBoardList(): List<BoardInListDTO> =
        withContext(ioDispatcher) {
            boardDao.getLocalCreateBoards()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationBoardList(): List<BoardDTO> =
        withContext(ioDispatcher) {
            boardDao.getLocalOperationBoards()
                .map { it.toDto() }
        }

    override suspend fun getArchivedBoardsByWorkspace(id: Long): Flow<List<BoardDTO>> =
        withContext(ioDispatcher) {
            boardDao.getAllBoardsArchived(id)
                .map { entities -> entities.map { it.toDto() } }
        }

    override suspend fun getWatchStatus(id: Long): Flow<Boolean> =
        withContext(ioDispatcher) {
            boardMemberDao.getBoardMemberAlarmFlow(id).map { it.toDTO().isAlert }
        }

    override suspend fun toggleBoardWatch(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val memberAlarm = boardMemberDao.getBoardMemberAlarm(id)

            if(memberAlarm != null) {
                if (isConnected) {
                    boardDataSource.toggleWatchBoard(id)
                } else {
                    val result = when(memberAlarm.isStatus) {
                        DataStatus.STAY ->
                            boardMemberDao.updateBoardMemberAlarm(memberAlarm.copy(
                                isStatus = DataStatus.UPDATE
                            ))
                        DataStatus.CREATE, DataStatus.UPDATE  ->
                            boardMemberDao.updateBoardMemberAlarm(memberAlarm.copy(
                                isAlert = !memberAlarm.isAlert
                            ))
                        DataStatus.DELETE -> { }
                    }

                    flowOf(result)
                }
            } else{
                flowOf(Unit)
            }
        }

    override suspend fun getBoardMembers(boardId: Long): Flow<List<MemberResponseDTO>> =
        withContext(ioDispatcher) {
            boardMemberDao.getBoardMembers(boardId)
                .map { list -> list.map { it.toDTO() } }
        }

    override suspend fun getLocalOperationBoardMember(): List<BoardMemberDTO> =
        withContext(ioDispatcher) {
            boardMemberDao.getLocalOperationBoardMember()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationBoardMemberAlarm(): List<BoardMemberAlarmDTO> =
        withContext(ioDispatcher) {
            boardMemberDao.getLocalOperationBoardMemberAlarm()
                .map { it.toDTO() }
        }
}

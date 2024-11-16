package com.ssafy.data.repository.board

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ssafy.data.di.IoDispatcher
import com.ssafy.database.dao.BoardDao
import com.ssafy.database.dao.BoardMemberDao
import com.ssafy.database.dao.LabelDao
import com.ssafy.database.dao.NegativeIdGenerator
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.BoardMemberAlarmEntity
import com.ssafy.database.dto.BoardMemberEntity
import com.ssafy.database.dto.LabelEntity
import com.ssafy.database.dto.bitmask.UpdateBoardBitmaskDTO
import com.ssafy.database.dto.piece.LocalTable
import com.ssafy.database.dto.piece.bitmaskColumn
import com.ssafy.database.dto.piece.toDTO
import com.ssafy.database.dto.piece.toDto
import com.ssafy.database.dto.piece.toEntity
import com.ssafy.model.activity.BoardActivity
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.MemberResponseDTO
import com.ssafy.model.board.UpdateBoardRequestDto
import com.ssafy.model.label.CreateLabelRequestDto
import com.ssafy.model.label.LabelDTO
import com.ssafy.model.label.UpdateLabelRequestDto
import com.ssafy.model.member.SimpleMemberDto
import com.ssafy.model.user.User
import com.ssafy.model.with.BoardInListDTO
import com.ssafy.model.with.BoardMemberAlarmDTO
import com.ssafy.model.with.BoardMemberDTO
import com.ssafy.model.with.DataStatus
import com.ssafy.network.source.board.BoardActivityPagingSource
import com.ssafy.network.source.board.BoardDataSource
import com.ssafy.nullable.CoverWithNull
import com.ssafy.nullable.UpdateBoardWithNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepositoryImpl @Inject constructor(
    private val boardDataSource: BoardDataSource,
    private val boardDao: BoardDao,
    private val boardMemberDao: BoardMemberDao,
    private val labelDao: LabelDao,
    private val negativeIdGenerator: NegativeIdGenerator,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BoardRepository {

    override suspend fun createOnlyBoard(myMemberId: Long, boardDTO: BoardDTO) {
        if (boardDTO.isStatus != DataStatus.STAY) {
            throw RuntimeException("이 보드는 워치 정보를 담기 위해 서버에서 생성된 보드여야 합니다.")
        }

        boardDao.insertBoard(boardDTO.toEntity())
    }

    override suspend fun createBoard(
        myMemberId: Long,
        boardDTO: BoardDTO,
        isConnected: Boolean
    ): Flow<Long> = withContext(ioDispatcher) {
        if (isConnected) {
            boardDataSource.createBoard(boardDTO).map { it.id }
        } else {
            val localBoardId = negativeIdGenerator.getNextNegativeId(LocalTable.BOARD)

            boardDao.insertBoard(
                boardDTO.copy(
                    id = localBoardId,
                    isStatus = DataStatus.CREATE
                ).toEntity()
            )

            createBoardMember(
                boardId = localBoardId,
                memberId = myMemberId,
                isConnected = false
            )

            createBoardWatch(
                boardId = localBoardId,
                isStatus = DataStatus.CREATE
            )

            flowOf(localBoardId)
        }
    }

    override suspend fun getBoard(boardId: Long): Flow<BoardDTO?> =
        withContext(ioDispatcher) {
            boardDao.getBoardFlow(boardId).map { it?.toDto() }
        }

    override suspend fun deleteBoard(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val board = boardDao.getBoard(id)

            if (board != null) {
                if (isConnected) {
                    boardDataSource.deleteBoard(id)
                } else {
                    val result = when (board.isStatus) {
                        DataStatus.CREATE ->
                            boardDao.deleteBoard(board)

                        else ->
                            boardDao.updateBoard(board.copy(isStatus = DataStatus.DELETE))
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun updateBoard(
        id: Long,
        updateBoardRequestDto: UpdateBoardRequestDto,
        isConnected: Boolean
    ): Flow<Unit> =
        withContext(ioDispatcher) {
            val board = boardDao.getBoard(id)

            if (board != null) {
                if (isConnected) {
                    boardDataSource.updateBoard(id, updateBoardRequestDto)
                } else {
                    // 변경 사항 확인하고 비트마스킹
                    val newBoard = board.copy(
                        name = updateBoardRequestDto.name,
                        coverType = updateBoardRequestDto.cover.type.name,
                        coverValue = updateBoardRequestDto.cover.value,
                        visibility = updateBoardRequestDto.visibility.name
                    )
                    val newBit = bitmaskColumn(board.columnUpdate, board, newBoard)

                    val result = when (board.isStatus) {
                        DataStatus.STAY, DataStatus.UPDATE ->
                            boardDao.updateBoard(
                                newBoard.copy(
                                    columnUpdate = newBit,
                                    isStatus = DataStatus.UPDATE
                                )
                            )

                        DataStatus.CREATE ->
                            boardDao.updateBoard(newBoard)

                        DataStatus.DELETE -> {}
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun setBoardArchive(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val board = boardDao.getBoard(id)

            if (board != null) {
                if (isConnected) {
                    boardDataSource.setBoardArchive(id)
                } else {
                    // 변경 사항 확인하고 비트마스킹
                    val newBoard = board.copy(isClosed = !board.isClosed)
                    val newBit = bitmaskColumn(board.columnUpdate, board, newBoard)

                    val result = when (board.isStatus) {
                        DataStatus.STAY, DataStatus.UPDATE ->
                            boardDao.updateBoard(
                                newBoard.copy(
                                    columnUpdate = newBit,
                                    isStatus = DataStatus.UPDATE
                                )
                            )

                        DataStatus.CREATE ->
                            boardDao.updateBoard(newBoard)

                        DataStatus.DELETE -> {}
                    }

                    flowOf(result)
                }
            } else {
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

    override suspend fun getLocalOperationBoardList(): List<BoardEntity> =
        withContext(ioDispatcher) { boardDao.getLocalOperationBoards() }

    override suspend fun getArchivedBoardsByWorkspace(id: Long): Flow<List<BoardDTO>> =
        withContext(ioDispatcher) {
            boardDao.getAllBoardsArchived(id)
                .map { entities -> entities.map { it.toDto() } }
        }

    override suspend fun createBoardWatch(boardId: Long, isStatus: DataStatus): Flow<Long> =
        withContext(ioDispatcher) {
            flowOf(
                boardMemberDao.insertBoardAlarm(
                    BoardMemberAlarmEntity(
                        boardId = boardId,
                        isStatus = isStatus
                    )
                )
            )
        }

    override suspend fun getWatchStatus(id: Long): Flow<Boolean?> =
        withContext(ioDispatcher) {
            boardMemberDao.getBoardMemberAlarmFlow(id).map { it?.toDTO()?.isAlert }
        }

    // 워치 변환은 서버에서만 가능
    override suspend fun toggleBoardWatch(
        memberId: Long,
        id: Long,
        isConnected: Boolean
    ): Flow<Unit> =
        withContext(ioDispatcher) {
            boardDataSource.toggleWatchBoard(id)
            val isAlert = boardDataSource.getWatchStatus(id).first()

            boardMemberDao.insertBoardMember(
                BoardMemberEntity(
                    boardId = id,
                    memberId = memberId,
                    isStatus = DataStatus.CREATE
                )
            )

            boardMemberDao.insertBoardAlarm(
                BoardMemberAlarmEntity(
                    boardId = id,
                    isStatus = DataStatus.STAY,
                    isAlert = isAlert
                )
            )

            flowOf()
        }

    override suspend fun getBoardMemberMyInfo(
        boardId: Long,
        memberId: Long
    ): Flow<BoardMemberDTO?> =
        withContext(ioDispatcher) {
            boardMemberDao.getBoardMemberFlow(boardId, memberId)
                .map { it?.toDTO() }
        }

    override suspend fun getBoardMembers(boardId: Long): Flow<List<MemberResponseDTO>> =
        withContext(ioDispatcher) {
            boardMemberDao.getBoardMembers(boardId)
                .map { list -> list.map { it.toDTO() } }
        }

    override suspend fun createBoardMember(
        boardId: Long,
        memberId: Long,
        isConnected: Boolean
    ): Flow<Long> =
        withContext(ioDispatcher) {
            if (isConnected) {
                boardDataSource.createBoardMember(boardId, memberId).map { it.memberId }
            } else {
                flowOf(
                    boardMemberDao.insertBoardMember(
                        BoardMemberEntity(
                            boardId = boardId,
                            memberId = memberId,
                            isStatus = DataStatus.CREATE
                        )
                    )
                )
            }
        }

    override suspend fun deleteBoardMember(
        boardId: Long,
        memberId: Long,
        isConnected: Boolean
    ): Flow<Unit> =
        withContext(ioDispatcher) {
            val boardMember = boardMemberDao.getBoardMember(boardId, memberId)

            if (boardMember != null) {
                if (isConnected) {
                    boardDataSource.deleteBoardMember(boardId, memberId).map { Unit }
                } else {
                    val result = when (boardMember.isStatus) {
                        DataStatus.CREATE -> boardMemberDao.deleteLocalBoardMember(
                            boardId,
                            memberId
                        )

                        else -> boardMemberDao.updateBoardMember(boardMember.copy(isStatus = DataStatus.DELETE))
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun updateBoardMember(
        boardId: Long,
        simpleMemberDto: SimpleMemberDto,
        isConnected: Boolean
    ): Flow<Unit> = withContext(ioDispatcher) {
        val boardMember = boardMemberDao.getBoardMember(boardId, simpleMemberDto.memberId)

        if (boardMember != null) {
            if (isConnected) {
                boardDataSource.updateBoardMember(boardId, simpleMemberDto).map { Unit }
            } else {
                val result = when (boardMember.isStatus) {
                    DataStatus.STAY ->
                        boardMemberDao.updateBoardMember(
                            boardMember.copy(
                                isStatus = DataStatus.UPDATE,
                                authority = simpleMemberDto.authority
                            )
                        )

                    DataStatus.CREATE, DataStatus.UPDATE ->
                        boardMemberDao.updateBoardMember(boardMember.copy(authority = simpleMemberDto.authority))

                    DataStatus.DELETE -> {}
                }

                flowOf(result)
            }
        } else {
            flowOf(Unit)
        }
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

    override suspend fun createLabel(
        boardId: Long,
        createLabelRequestDto: CreateLabelRequestDto,
        isConnected: Boolean
    ): Flow<Long> =
        withContext(ioDispatcher) {
            if (isConnected) {
                boardDataSource.createLabel(boardId, createLabelRequestDto).map { it.labelId }
            } else {
                flowOf(
                    labelDao.insertLabel(
                        LabelEntity(
                            boardId = boardId,
                            name = createLabelRequestDto.name,
                            color = createLabelRequestDto.color,
                            isStatus = DataStatus.CREATE
                        )
                    )
                )
            }
        }

    override suspend fun getLabel(id: Long): Flow<LabelDTO?> =
        withContext(ioDispatcher) {
            labelDao.getLabelFlow(id).map { it?.toDTO() }
        }

    override suspend fun getLabels(boardId: Long): Flow<List<LabelDTO>> =
        withContext(ioDispatcher) {
            labelDao.getAllLabels(boardId)
                .map { list -> list.map { it.toDTO() } }
        }

    override suspend fun deleteLabel(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            val label = labelDao.getLabel(id)

            if (label != null) {
                if (isConnected) {
                    boardDataSource.deleteLabel(id)
                } else {
                    val result = when (label.isStatus) {
                        DataStatus.CREATE ->
                            labelDao.deleteLabel(label)

                        else ->
                            labelDao.updateLabel(label.copy(isStatus = DataStatus.DELETE))
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun updateLabel(
        id: Long,
        updateLabelRequestDto: UpdateLabelRequestDto,
        isConnected: Boolean
    ): Flow<Unit> =
        withContext(ioDispatcher) {
            val label = labelDao.getLabel(id)

            if (label != null) {
                if (isConnected) {
                    boardDataSource.updateLabel(id, updateLabelRequestDto).map { Unit }
                } else {
                    // 변경 사항 확인하고 비트마스킹
                    val newLabel = label.copy(
                        name = updateLabelRequestDto.name,
                        color = updateLabelRequestDto.color
                    )
                    val newBit = bitmaskColumn(label.columnUpdate, label, newLabel)

                    val result = when (label.isStatus) {
                        DataStatus.STAY, DataStatus.UPDATE ->
                            labelDao.updateLabel(
                                newLabel.copy(
                                    columnUpdate = newBit,
                                    isStatus = DataStatus.UPDATE
                                )
                            )

                        DataStatus.CREATE ->
                            labelDao.updateLabel(newLabel)

                        DataStatus.DELETE -> {}
                    }

                    flowOf(result)
                }
            } else {
                flowOf(Unit)
            }
        }

    override suspend fun getLocalCreateLabels(): List<LabelDTO> =
        withContext(ioDispatcher) {
            labelDao.getLocalCreateLabels()
                .map { it.toDTO() }
        }

    override suspend fun getLocalOperationLabels(): List<LabelDTO> =
        withContext(ioDispatcher) {
            labelDao.getLocalOperationLabels()
                .map { it.toDTO() }
        }

    override suspend fun updateBoard(
        id: Long,
        updateBoardRequestDto: UpdateBoardBitmaskDTO
    ): Flow<Unit> = withContext(ioDispatcher) {
        val dto = UpdateBoardWithNull(
            name = updateBoardRequestDto.name,
            cover = updateBoardRequestDto.cover?.let { CoverWithNull(it.type, it.value) },
            visibility = updateBoardRequestDto.visibility
        )
        boardDataSource.updateBoard(id, dto)
    }

    override suspend fun getBoardActivity(boardId: Long): Flow<PagingData<BoardActivity>> = Pager(
        config = PagingConfig(
            pageSize = BoardActivityPagingSource.PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            BoardActivityPagingSource(
                boardId = boardId,
                boardDataSource = boardDataSource,
            )
        }
    ).flow.flowOn(ioDispatcher)

    override suspend fun getAllBoardAndWorkspaceMember(
        workspaceId: Long,
        boardId: Long
    ): Flow<List<User>> =
        withContext(ioDispatcher) {
            boardMemberDao.getAllBoardAndWorkspaceMember(
                workspaceId = workspaceId,
                boardId = boardId
            ).map { it.map { it.toDTO() } }
        }
}

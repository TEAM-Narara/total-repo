package com.ssafy.data.repository.board

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toFlow
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.BoardDetailResponseDtoList
import com.ssafy.model.board.MemberListResponseDTO
import com.ssafy.network.source.board.BoardDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepositoryImpl @Inject constructor(
    private val boardDataSource: BoardDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BoardRepository {

    override suspend fun createBoard(isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                boardDataSource.createBoard().toFlow()
            } else {
                TODO("Room DB 연동이 되면 로컬 데이터를 생성하는 로직을 추가해주세요.")
            }
        }

    override suspend fun getBoard(id: Long): Flow<BoardDTO> =
        withContext(ioDispatcher) {
            TODO("Room DB 연동이 되면 로컬 데이터를 가져오는 로직을 추가해주세요.")
        }

    override suspend fun deleteBoard(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                boardDataSource.deleteBoard(id).toFlow()
            } else {
                TODO("Room DB 연동이 되면 로컬 데이터를 삭제하는 로직을 추가해주세요.")
            }
        }

    override suspend fun updateBoard(boardDTO: BoardDTO, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                boardDataSource.updateBoard(boardDTO.id, boardDTO).toFlow()
            } else {
                TODO("Room DB 연동이 되면 로컬 데이터를 업데이트하는 로직을 추가해주세요.")
            }
        }

    override suspend fun setBoardArchive(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                boardDataSource.setBoardArchive(id).toFlow()
            } else {
                TODO("Room DB 연동이 되면 로컬 데이터를 업데이트하는 로직을 추가해주세요.")
            }
        }

    override suspend fun getBoardsByWorkspace(id: Long): Flow<BoardDetailResponseDtoList> =
        withContext(ioDispatcher) {
            TODO("워크스페이스에 있는 보드를 가져오는 로직을 추가해주세요.")
        }

    override suspend fun getArchivedBoardsByWorkspace(id: Long): Flow<BoardDetailResponseDtoList> =
        withContext(ioDispatcher) {
            TODO("워크스페이스에 있는 아카이브된 보드를 가져오는 로직을 추가해주세요.")
        }

    override suspend fun getWatchStatus(id: Long): Flow<Boolean> =
        withContext(ioDispatcher) {
            TODO("보드의 관심 상태를 가져오는 로직을 추가해주세요.")
        }

    override suspend fun toggleBoardWatch(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                boardDataSource.toggleWatchBoard(id).toFlow()
            } else {
                TODO("Room DB 연동이 되면 로컬 데이터를 업데이트하는 로직을 추가해주세요.")
            }
        }

    override suspend fun getBoardMembers(id: Long): Flow<MemberListResponseDTO> =
        withContext(ioDispatcher) {
            TODO("보드의 멤버를 가져오는 로직을 추가해주세요.")
        }

}

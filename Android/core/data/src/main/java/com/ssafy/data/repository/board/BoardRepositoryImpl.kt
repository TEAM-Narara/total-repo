package com.ssafy.data.repository.board

import com.ssafy.data.di.IoDispatcher
import com.ssafy.data.response.toFlow
import com.ssafy.model.board.BoardDTO
import com.ssafy.model.board.BoardDetailResponseDtoList
import com.ssafy.model.board.BoardMemberResponseDtoList
import com.ssafy.network.source.board.BoardDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoardRepositoryImpl @Inject constructor(
    private val boardDataSource: BoardDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BoardRepository {

    override suspend fun createBoard(isConnected: Boolean): Flow<BoardDTO> =
        withContext(ioDispatcher) {
            if (isConnected) {
                boardDataSource.createBoard()
                    .toFlow()
                    .map { boardDTO: BoardDTO ->
                        TODO("Room DB 연동이 되면 로컬 데이터를 생성하는 로직을 추가해주세요.")
                    }
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
                boardDataSource.deleteBoard(id)
                    .toFlow()
                    .map {
                        TODO("Room DB 연동이 되면 로컬 데이터를 삭제하는 로직을 추가해주세요.")
                    }
            } else {
                TODO("Room DB 연동이 되면 로컬 데이터를 삭제하는 로직을 추가해주세요.")
            }
        }

    override suspend fun updateBoard(boardDTO: BoardDTO, isConnected: Boolean): Flow<BoardDTO> =
        withContext(ioDispatcher) {
            if (isConnected) {
                boardDataSource.updateBoard(boardDTO.id, boardDTO)
                    .toFlow()
                    .map { boardDTO: BoardDTO ->
                        TODO("Room DB 연동이 되면 로컬 데이터를 업데이트하는 로직을 추가해주세요.")
                    }
            } else {
                TODO("Room DB 연동이 되면 로컬 데이터를 업데이트하는 로직을 추가해주세요.")
            }
        }

    override suspend fun 이게뭐하는함수인교(id: Long, isConnected: Boolean): Flow<Unit> {
        TODO("얜 뭐하는 함수인지 모르겄네 이거 왜만들어야 하지")
    }

    override suspend fun setBoardArchive(id: Long, isConnected: Boolean): Flow<Unit> =
        withContext(ioDispatcher) {
            if (isConnected) {
                boardDataSource.setBoardArchive(id)
                    .toFlow()
                    .map {
                        TODO("Room DB 연동이 되면 로컬 데이터를 업데이트하는 로직을 추가해주세요.")
                    }
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
                boardDataSource.toggleWatchBoard(id)
                    .toFlow()
                    .map {
                        TODO("Room DB 연동이 되면 로컬 데이터를 업데이트하는 로직을 추가해주세요.")
                    }
            } else {
                TODO("Room DB 연동이 되면 로컬 데이터를 업데이트하는 로직을 추가해주세요.")
            }
        }

    override suspend fun getBoardMembers(id: Long): Flow<BoardMemberResponseDtoList> =
        withContext(ioDispatcher) {
            TODO("보드의 멤버를 가져오는 로직을 추가해주세요.")
        }

}

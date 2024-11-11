package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ToggleBoardWatchUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(boardId: Long, isConnected: Boolean): Flow<Unit> = flow {
        if (!isConnected) {
            throw RuntimeException("보드의 알림을 받기 위해서는 온라인이여야 합니다.")
        }

        boardRepository.toggleBoardWatch(boardId, true).collect { emit(it) }
    }

}

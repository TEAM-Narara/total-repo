package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleBoardWatchUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(boardId: Long, isConnected: Boolean): Flow<Unit> {
        return boardRepository.toggleBoardWatch(boardId, isConnected)
    }

}

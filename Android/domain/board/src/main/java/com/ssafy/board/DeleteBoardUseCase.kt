package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteBoardUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(id: Long, isConnected: Boolean): Flow<Unit> {
        return boardRepository.deleteBoard(id, isConnected)
    }

}

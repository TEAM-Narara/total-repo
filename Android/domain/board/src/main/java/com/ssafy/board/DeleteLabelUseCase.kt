package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteLabelUseCase @Inject constructor(
    val boardRepository: BoardRepository
) {
    suspend operator fun invoke(
        boardId: Long,
        isConnected: Boolean
    ): Flow<Unit> {
        return boardRepository.deleteLabel(boardId, isConnected)
    }
}
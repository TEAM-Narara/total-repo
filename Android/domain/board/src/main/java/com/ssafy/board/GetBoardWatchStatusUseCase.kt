package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBoardWatchStatusUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(boardId: Long): Flow<Boolean> {
        return boardRepository.getWatchStatus(boardId) ?: throw Exception("존재하지 않는 board 입니다.")
    }

}

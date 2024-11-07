package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.model.board.UpdateBoardRequestDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateBoardUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(
        id: Long,
        updateBoardRequestDto: UpdateBoardRequestDto,
        isConnected: Boolean
    ): Flow<Unit> {
        return boardRepository.updateBoard(id, updateBoardRequestDto, isConnected)
    }

}

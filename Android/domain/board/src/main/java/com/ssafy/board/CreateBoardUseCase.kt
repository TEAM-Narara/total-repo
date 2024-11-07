package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.model.board.BoardDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateBoardUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(boardDTO: BoardDTO, isConnected: Boolean): Flow<Long> {
        return boardRepository.createBoard(boardDTO, isConnected)
    }

}

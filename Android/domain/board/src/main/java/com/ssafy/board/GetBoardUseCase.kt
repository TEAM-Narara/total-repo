package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.model.board.BoardDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBoardUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(boardId: Long): Flow<BoardDTO?> {
        return  boardRepository.getBoard(boardId)
    }

}

package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.model.board.BoardDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBoardUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(id: Long): Flow<BoardDTO> {
        return boardRepository.getBoard(id) ?: throw Exception("존재하지 않는 board 입니다.")
    }

}

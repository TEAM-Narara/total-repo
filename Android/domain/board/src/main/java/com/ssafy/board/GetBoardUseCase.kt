package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.data.socket.board.BoardStomp
import com.ssafy.model.board.BoardDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBoardUseCase @Inject constructor(
    private val boardStomp: BoardStomp,
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(boardId: Long): Flow<BoardDTO?> {
        boardStomp.connect(boardId)
        return  boardRepository.getBoard(boardId)
    }

}

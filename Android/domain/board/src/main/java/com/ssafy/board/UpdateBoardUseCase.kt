package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.model.background.Cover
import com.ssafy.model.board.UpdateBoardRequestDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
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

    suspend operator fun invoke(id: Long, cover: Cover, isConnected: Boolean) = flow {
        val error = "보드를 찾을 수 없습니다."
        val boardInfo =
            boardRepository.getBoard(id).firstOrNull() ?: throw IllegalArgumentException(error)

        val updateBoardRequestDto = UpdateBoardRequestDto(
            name = boardInfo.name,
            cover = cover,
            visibility = boardInfo.visibility
        )

        boardRepository.updateBoard(id, updateBoardRequestDto, isConnected).collect { emit(it) }
    }

}

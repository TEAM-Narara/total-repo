package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import javax.inject.Inject

class GetLabelUseCase @Inject constructor(
    val boardRepository: BoardRepository
) {
    suspend operator fun invoke(boardId: Long) = boardRepository.getLabels(boardId)
}
package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.model.label.UpdateLabelRequestDto
import javax.inject.Inject

class UpdateLabelUseCase @Inject constructor(
    val boardRepository: BoardRepository
) {
    suspend operator fun invoke(
        boardId: Long,
        updateLabelRequestDto: UpdateLabelRequestDto,
        isConnected: Boolean
    ) {
        boardRepository.updateLabel(boardId, updateLabelRequestDto, isConnected)
    }
}
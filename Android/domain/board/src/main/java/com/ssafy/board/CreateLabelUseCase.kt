package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.model.label.CreateLabelRequestDto
import com.ssafy.model.label.LabelDTO
import javax.inject.Inject

class CreateLabelUseCase @Inject constructor(
    val boardRepository: BoardRepository
) {
    suspend operator fun invoke(createLabelRequestDto: CreateLabelRequestDto, isConnected: Boolean) {
//        boardRepository.createLabel(createLabelRequestDto, isConnected)
    }
}
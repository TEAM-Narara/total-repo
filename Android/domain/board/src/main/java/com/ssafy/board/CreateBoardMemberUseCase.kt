package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import javax.inject.Inject

class CreateBoardMemberUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(boardId: Long, memberId: Long, isConnected: Boolean) {
        boardRepository.createBoardMember(boardId, memberId, isConnected)
    }

}
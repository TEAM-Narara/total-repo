package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.model.board.MemberListResponseDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBoardMembersUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(boardId: Long): Flow<MemberListResponseDTO> {
        return boardRepository.getBoardMembers(boardId)
    }

}

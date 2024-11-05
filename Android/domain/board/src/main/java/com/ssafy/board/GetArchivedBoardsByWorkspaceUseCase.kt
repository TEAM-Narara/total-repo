package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.model.board.BoardDetailResponseDtoList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArchivedBoardsByWorkspaceUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(workspaceId: Long): Flow<BoardDetailResponseDtoList> {
        return boardRepository.getArchivedBoardsByWorkspace(workspaceId)
    }

}

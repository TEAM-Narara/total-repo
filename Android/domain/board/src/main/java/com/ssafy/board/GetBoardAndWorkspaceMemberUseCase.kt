package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.model.user.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBoardAndWorkspaceMemberUseCase @Inject constructor(
    private val boardRepository: BoardRepository,
) {
    suspend operator fun invoke(workspaceId: Long, boardId: Long): Flow<List<User>> =
        boardRepository.getAllBoardAndWorkspaceMember(workspaceId, boardId)
}
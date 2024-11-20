package com.ssafy.board

import com.ssafy.data.repository.board.BoardRepository
import com.ssafy.model.member.Authority
import com.ssafy.model.member.SimpleMemberDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateBoardMemberUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {

    suspend operator fun invoke(
        boardId: Long,
        memberId: Long,
        authority: Authority,
        isConnected: Boolean
    ): Flow<Unit> {
        val simpleMemberDto = SimpleMemberDto(
            memberId = memberId,
            authority = authority,
        )

        return boardRepository.updateBoardMember(boardId, simpleMemberDto, isConnected)
    }
}

package com.ssafy.workspace

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.model.member.SimpleMemberDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangeWorkspaceMemberUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {

    suspend operator fun invoke(
        workspaceId: Long,
        memberId: Long,
        authority: String,
        isConnected: Boolean
    ): Flow<Unit> {
        val simpleMemberDto = SimpleMemberDto(memberId, authority)
        return workspaceRepository.updateWorkspaceMember(workspaceId, simpleMemberDto, isConnected)
    }

}
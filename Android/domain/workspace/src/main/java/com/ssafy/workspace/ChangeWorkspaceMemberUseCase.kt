package com.ssafy.workspace

import com.ssafy.data.repository.workspace.WorkspaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ChangeWorkspaceMemberUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {

    suspend operator fun invoke(
        workspaceId: Long,
        memberId: Long,
        authority: String
    ): Flow<Unit> {
//        workspaceRepository.updateWorkspaceMember(id, memberId, authority)
//        return workspaceRepository.changeWorkspaceMember(workspaceId, simpleMemberDto)
        return flowOf(Unit)
    }

}
package com.ssafy.workspace

import com.ssafy.data.repository.workspace.WorkspaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteWorkspaceUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {

    suspend operator fun invoke(workspaceId: Long, isConnected: Boolean): Flow<Unit> {
        return workspaceRepository.deleteWorkspace(workspaceId, isConnected)
    }

}

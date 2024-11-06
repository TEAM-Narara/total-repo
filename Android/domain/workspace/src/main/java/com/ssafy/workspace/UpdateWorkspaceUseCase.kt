package com.ssafy.workspace

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.model.workspace.WorkspaceRequestDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateWorkspaceUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {

    suspend operator fun invoke(
        workspaceId: Long,
        name: String,
        isConnected: Boolean
    ): Flow<Unit> {
        return workspaceRepository.updateWorkspace(workspaceId, name, isConnected)
    }

}

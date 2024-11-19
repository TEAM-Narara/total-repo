package com.ssafy.workspace

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.model.workspace.WorkSpaceDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkspaceUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {

    suspend operator fun invoke(workspaceId: Long): Flow<WorkSpaceDTO?> =
        workspaceRepository.getWorkspace(workspaceId)

}

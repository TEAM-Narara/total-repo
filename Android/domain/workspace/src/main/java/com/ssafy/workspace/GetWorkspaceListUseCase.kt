package com.ssafy.workspace

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.model.workspace.WorkSpaceDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkspaceListUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {

    suspend operator fun invoke(isConnected: Boolean): Flow<List<WorkSpaceDTO>> {
        return workspaceRepository.getWorkspaceList(isConnected)
    }

}

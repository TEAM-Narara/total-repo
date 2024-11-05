package com.ssafy.workspace

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.model.workspace.WorkSpaceListResponseDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkspaceListUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {

    suspend operator fun invoke(isConnected: Boolean): Flow<WorkSpaceListResponseDTO> {
        return workspaceRepository.getWorkspaceList(isConnected)
    }

}

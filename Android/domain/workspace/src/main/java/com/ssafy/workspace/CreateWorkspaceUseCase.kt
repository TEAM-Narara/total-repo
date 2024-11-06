package com.ssafy.workspace

import com.ssafy.data.repository.workspace.WorkspaceRepository
import com.ssafy.model.workspace.WorkspaceRequestDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateWorkspaceUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {

//    suspend operator fun invoke(
//        workspaceRequestDTO: WorkspaceRequestDTO,
//        isConnected: Boolean
//    ): Flow<Unit> {
//        return workspaceRepository.createWorkspace(workspaceRequestDTO, isConnected)
//    }

}
